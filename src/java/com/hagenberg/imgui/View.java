package com.hagenberg.imgui;

import org.lwjgl.glfw.GLFW;

import imgui.ImGui;
import imgui.type.ImBoolean;

public abstract class View {
  private String name = "View";
  private int flags = 0;
  private ImBoolean showWindow = new ImBoolean(true);

  public void render() {
    if (!ImGui.begin(name, showWindow, flags)) {
      ImGui.end();
      return;
    }
    renderWindow();
    ImGui.end();
  }

  protected void setName(String name) {
    this.name = name;
  }

  protected void setFlags(int flags) {
    this.flags = flags;
  }

  public void setShowWindow(boolean showWindow) {
    this.showWindow.set(showWindow);
  }

  public boolean getShowWindow() {
    return showWindow.get();
  }

  protected abstract void renderWindow();
}
