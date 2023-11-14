package com.hagenberg.imgui.components;

import com.hagenberg.jarvis.util.Procedure;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;

public class Tooltip {
  public void show(Procedure procedure) {
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5); // NodeEditor somehow overrides this so we have to set it here
    if (ImGui.isItemHovered()) {
      ImGui.beginTooltip();
      procedure.run();
      ImGui.endTooltip();
    }
    ImGui.popStyleVar();
  }
}
