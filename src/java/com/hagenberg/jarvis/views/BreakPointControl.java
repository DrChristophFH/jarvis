package com.hagenberg.jarvis.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.debugger.BreakPointProvider;

import imgui.ImGui;
import imgui.type.ImInt;

public class BreakPointControl extends View implements BreakPointProvider {

  private String classPath;
  private Map<String, List<Integer>> classNames = new HashMap<>();
  private String selected;
  private ImInt line = new ImInt();

  public BreakPointControl() {

  }

  public List<Integer> getBreakPoints(String className) {
    return classNames.get(className);
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

    if (ImGui.beginListBox(classPath)) {
      for (String className : classNames.keySet()) {
        if(ImGui.selectable(className)) {
          selected = className;
        }
      }
      ImGui.endListBox();
    }

    if (selected != null) {
      ImGui.text("Breakpoints for " + selected);
      ImGui.separator();
      StringBuffer sb = new StringBuffer();
      for (Integer line : classNames.get(selected)) {
        sb.append(line + ", ");
      }
      ImGui.text(sb.toString());
      ImGui.inputInt("Line", line);
      if (ImGui.button("Add")) {
        classNames.get(selected).add(line.get());
      }
    }
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
        classNames.put(className, new ArrayList<>());
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
          classNames.put(className, new ArrayList<>());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
