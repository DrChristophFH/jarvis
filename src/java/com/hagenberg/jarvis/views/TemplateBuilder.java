package com.hagenberg.jarvis.views;

import java.util.List;

import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.graph.IdProvider;
import com.hagenberg.jarvis.graph.rendering.Path;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.graph.rendering.renderers.specific.TemplateRenderer;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseCursor;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

public class TemplateBuilder extends View {
  
  private RendererRegistry registry;
  private IdProvider ids;
  private TemplateRenderer selectedTemplate;
  private Path selectedPath;
  private ImString input = new ImString();
  private ImString name = new ImString();
  private int[] width = { 200 };

  public TemplateBuilder(RendererRegistry registry, IdProvider ids) {
    setName("Template Builder");
    setFlags(ImGuiWindowFlags.MenuBar);
    this.registry = registry;
    this.ids = ids;
  }

  @Override
  protected void renderWindow() {
    float availableWidth = ImGui.getContentRegionAvailX();
    ImGui.setNextItemWidth(availableWidth);
    ImGui.sliderInt("width", width, 0, (int) availableWidth);
    if (ImGui.isItemHovered()) {
      ImGui.setMouseCursor(ImGuiMouseCursor.ResizeEW);
    }

    if (ImGui.beginMenuBar()) {
      if (ImGui.beginMenu("File")) {
        if (ImGui.menuItem("New Template")) {
          registry.getTemplates().add(new TemplateRenderer("New Template", registry, ids));
        } 
        ImGui.endMenu();
      }
      ImGui.endMenuBar();
    }

    ImGui.beginChild("left pane", width[0], 0, true);
    displayList();
    ImGui.endChild();

    ImGui.sameLine();

    ImGui.beginChild("right pane", 0, 0, true);
    if (selectedTemplate != null) {
      displayTemplate(selectedTemplate);
    }
    ImGui.endChild();
  }

  private void displayList() {
    for (TemplateRenderer template : registry.getTemplates()) {
      if (ImGui.selectable(template.getName(), template == selectedTemplate)) {
        selectedTemplate = template;
        selectedPath = null;
        input.set("");
        name.set(template.getName());
      }
    }
  }

  private void displayTemplate(TemplateRenderer selectedTemplate) {
    if (ImGui.inputText("Name", name)) {
      selectedTemplate.setName(name.get());
    }

    ImGui.separator();

    ImGui.text("Paths:");
    if (ImGui.beginListBox("##Paths")) {
      for (Path path : selectedTemplate.getPaths()) {
        if (ImGui.selectable(path.toString(), path == selectedPath)) {
          selectedPath = path;
          input.set(path.toString());
        }
      }
      ImGui.endListBox();
    }

    if (ImGui.inputText("Path", input, ImGuiInputTextFlags.EnterReturnsTrue)) {
      if (selectedPath != null) {
        selectedPath.setPath(input.get());
      } else {
        selectedTemplate.getPaths().add(new Path(input.get()));
      }
    }

    if (ImGui.button("Add Path")) {
      selectedTemplate.getPaths().add(new Path(List.of()));
    }
    if (selectedPath != null) {
      ImGui.sameLine();
      if (ImGui.button("Remove Path")) {
        selectedTemplate.getPaths().remove(selectedPath);
        selectedPath = null;
      }
    }
  }
}
