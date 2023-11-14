package com.hagenberg.imgui;

import com.hagenberg.imgui.components.Tooltip;

import imgui.ImGui;
import imgui.type.ImBoolean;

public abstract class View {
  private String name = "View";
  private int flags = 0;
  private ImBoolean showWindow = new ImBoolean(true);

  protected float[] flContainer = new float[1]; // used for sliders
  protected int[] iContainer = new int[1]; // used for sliders
  protected Tooltip tooltip = new Tooltip();

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

  /**
   * Helper to create a float slider control. Result is stored in the flContainer array.
   * 
   * @param label  the label to display next to the slider control
   * @param value  the initial value of the slider control
   * @param min    the minimum value of the slider control
   * @param max    the maximum value of the slider control
   * @param format the format string used to display the value of the slider control
   * @return true if the user changed the value of the slider control, false otherwise
   */
  protected final boolean sliderFloat(String label, float value, float min, float max, String format) {
    flContainer[0] = value;
    return ImGui.sliderFloat(label, flContainer, min, max, format);
  }

  /**
   * Helper to create an int slider control. Result is stored in the iContainer array.
   * 
   * @param label the label to display next to the slider
   * @param value the current value of the slider
   * @param min   the minimum value of the slider
   * @param max   the maximum value of the slider
   * @return true if the slider value was changed, false otherwise
   */
  protected final boolean sliderInt(String label, int value, int min, int max) {
    iContainer[0] = value;
    return ImGui.sliderInt(label, iContainer, min, max);
  }
}
