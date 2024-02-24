package com.hagenberg.jarvis.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.config.AppConfig;
import com.hagenberg.jarvis.config.ConfigManager;
import com.hagenberg.jarvis.debugger.BreakPointProvider;
import com.hagenberg.jarvis.models.entities.BreakPoint;

import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiKey;
import imgui.internal.flag.ImGuiAxis;
import imgui.type.ImInt;

public class BreakPointControl extends View implements BreakPointProvider {

  private String classPath;
  private Set<String> classNames = new TreeSet<>();
  private Map<String, List<BreakPoint>> breakPointMap = new TreeMap<>();
  private String selected;
  private ImInt line = new ImInt();
  private boolean abbreviate = true;

  public BreakPointControl() {
    setName("Breakpoint Control");
    loadBreakPoints();
  }

  public List<BreakPoint> getBreakPoints(String className) {
    return breakPointMap.get(className).stream().filter(BreakPoint::isEnabled).toList();
  }

  public void setClassPath(String classPath) {
    this.classPath = classPath;
    refresh();
  }

  @Override
  protected void renderWindow() {
    if (classPath == null) {
      ImGui.text("No classpath set!");
      return;
    }

    if (ImGui.button("Refresh")) {
      refresh();
    }

    if (classNames.isEmpty()) {
      ImGui.text("No classes found!");
      return;
    }

    ImGui.text(classPath);

    displayClassList();

    if (selected != null) {
      displayBreakPointSet();
    }

    ImGui.separator();
    displayBreakPointOverview();
  }

  private void displayBreakPointOverview() {
    if (ImGui.button("Disable All")) {
      for (String className : breakPointMap.keySet()) {
        for (BreakPoint bp : breakPointMap.get(className)) {
          bp.setEnabled(false);
        }
      }
      saveBreakPoints();
    }

    ImGui.sameLine();

    if (ImGui.button("Enable All")) {
      for (String className : breakPointMap.keySet()) {
        for (BreakPoint bp : breakPointMap.get(className)) {
          bp.setEnabled(true);
        }
      }
      saveBreakPoints();
    }

    ImGui.sameLine();

    if (ImGui.button("Delete All")) {
      breakPointMap.clear();
      saveBreakPoints();
    }

    ImGui.sameLine();

    if (ImGui.checkbox("Abbreviate", abbreviate)) {
      abbreviate = !abbreviate;
    }

    BreakPoint delete = null;

    for (String className : breakPointMap.keySet()) {
      if (!breakPointMap.get(className).isEmpty()) {
        for (BreakPoint bp : breakPointMap.get(className)) {
          String name = abbreviate ? className.substring(className.lastIndexOf(".") + 1) : className;

          if (ImGui.checkbox(name + " : " + bp.getLine(), bp.isEnabled())) {
            bp.setEnabled(!bp.isEnabled());
          }

          ImGui.sameLine();

          if (ImGui.button("Delete")) {
            delete = bp;
          }
        }
        if (delete != null) { // here to avoid concurrent modification exception
          breakPointMap.get(className).remove(delete);
        }
      }
    }

    if (delete != null) { // here to avoid concurrent modification exception
      saveBreakPoints();
    }
  }

  private void displayBreakPointSet() {
    ImGui.separator();
    ImGui.text(selected);
    StringBuffer sb = new StringBuffer();
    for (BreakPoint bp : breakPointMap.getOrDefault(selected, new ArrayList<>())) {
      sb.append(bp.getLine() + ", ");
    }
    ImGui.text("Breakpoints: " + sb.toString());
    int flags = ImGuiInputTextFlags.AutoSelectAll;
    
    ImGui.inputInt("Line", line, 1, 10, flags);

    if (ImGui.isItemDeactivated() && (Snippets.isKeyDown(ImGuiKey.Enter) || Snippets.isKeyDown(ImGuiKey.KeyPadEnter)) || ImGui.button("Add")) {
      if (!breakPointMap.containsKey(selected)) {
        breakPointMap.put(selected, new ArrayList<>());
      }
      breakPointMap.get(selected).add(new BreakPoint(line.get()));
      saveBreakPoints();
    }
  }

  private void displayClassList() {
    if (ImGui.beginListBox("##Classes", -1, 0)) {
      for (String className : classNames) {
        if(ImGui.selectable(className)) {
          selected = className;
        }
      }
      ImGui.endListBox();
    }
  }

  private void saveBreakPoints() {
    ConfigManager configManager = ConfigManager.getInstance();
    AppConfig config = configManager.getConfig();
    config.setBreakPoints(new TreeMap<>());
    for (String className : breakPointMap.keySet()) {
      if (!breakPointMap.get(className).isEmpty()) {
        config.getBreakPoints().put(className, breakPointMap.get(className));
      }
    }
    configManager.saveConfig();
  }

  private void loadBreakPoints() {
    ConfigManager configManager = ConfigManager.getInstance();
    AppConfig config = configManager.getConfig();
    breakPointMap = config.getBreakPoints();
  }

  private void refresh() {
    classNames.clear();
    File entryFile = new File(classPath);
    if (entryFile.isDirectory()) {
      processDirectory(entryFile, "");
    } else if (classPath.endsWith(".jar")) {
      processJarFile(entryFile);
    }
  }

  private void processDirectory(File directory, String pkg) {
    for (File file : directory.listFiles()) {
      if (file.isDirectory()) {
        if (pkg.isEmpty()) {
          processDirectory(file, file.getName());
        } else {
          processDirectory(file, pkg + "." + file.getName());
        }
      } else if (file.getName().endsWith(".class")) {
        String className = pkg + "." + file.getName().replace(".class", "");
        classNames.add(className);
      }
    }
  }

  private void processJarFile(File jarFile) {
    try (JarFile jar = new JarFile(jarFile)) {
      Enumeration<JarEntry> entries = jar.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        if (entry.getName().endsWith(".class")) {
          String className = entry.getName().replace("/", ".").replace(".class", "");
          classNames.add(className);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
