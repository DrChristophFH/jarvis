package com.hagenberg.imgui;

import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;

public class Colors {
  // usage specific colors
  public static final int AccessModifier = ImColor.rgb("#FFA500");
  public static final int Type = ImColor.rgb("#44C9B0");
  public static final int Identifier = ImColor.rgb("#4FC1FF");

  // imnodes colors
  public static final int LocalVariable = ImColor.rgb("#0D1117");
  public static final int LinkSelected = ImColor.rgb("#FFA500");

  // text colors
  public static final int Text = ImGui.getColorU32(ImGuiCol.Text);
  public static final int Attention = ImColor.rgb("#E9B20C");
  public static final int Error = ImColor.rgb("#FF0000");

  // general purpose colors
  public static final int Invisible = ImColor.rgba("#00000000");
}
