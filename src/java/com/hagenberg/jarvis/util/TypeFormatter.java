package com.hagenberg.jarvis.util;

import com.hagenberg.imgui.Colors;
import com.sun.jdi.Type;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;

public class TypeFormatter {
  public static String getSimpleType(String type) {
    String[] parts = type.split("\\.");
    return parts[parts.length - 1];
  }

  public static String getSimpleType(Type type) {
    return type.name().substring(type.name().lastIndexOf(".") + 1);
  }

  public static void drawTypeWithTooltip(Type type) {
    String simpleType = getSimpleType(type);
    ImGui.textColored(Colors.Type, simpleType);
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5); // NodeEditor somehow overrides this so we have to set it here
    if (ImGui.isItemHovered()) {
      ImGui.beginTooltip();
      ImGui.text(type.name());
      ImGui.endTooltip();
    }
    ImGui.popStyleVar();
  }
}
