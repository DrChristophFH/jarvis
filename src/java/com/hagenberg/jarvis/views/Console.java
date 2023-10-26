package com.hagenberg.jarvis.views;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.debugger.DebugeeConsole;
import com.hagenberg.jarvis.debugger.InputHandler;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

public class Console extends View implements DebugeeConsole {

  private List<String> items = new ArrayList<>();
  private boolean scrollToBottom = true;
  private boolean autoScroll = true;
  private ImString inputBuf = new ImString();

  private InputHandler inputHandler;

  public Console() {
    setName("Console");
  }

  public void println(String text) {
    items.add(text);
  }
  
  @Override
  public void registerInputHandler(InputHandler handler) {
    this.inputHandler = handler;
  }

  @Override
  protected void renderWindow() {
    // Reserve enough left-over height for 1 separator + 1 input text
    float footerHeightToReserve = ImGui.getStyle().getItemSpacingY() + ImGui.getFrameHeightWithSpacing();
    if (ImGui.beginChild("ScrollingRegion", 0, -footerHeightToReserve, false, ImGuiWindowFlags.HorizontalScrollbar)) {
      if (ImGui.beginPopupContextWindow()) {
        if (ImGui.selectable("Clear")) {
          clearLog();
        }
        ImGui.endPopup();
      }

      ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 4, 1); // Tighten spacing
      for (String line : items) {
        ImGui.textUnformatted(line);
      }

      if (scrollToBottom || (autoScroll && ImGui.getScrollY() >= ImGui.getScrollMaxY())) {
        ImGui.setScrollHereY(1.0f);
      }
      scrollToBottom = false;

      ImGui.popStyleVar();
    }
    ImGui.endChild();
    ImGui.separator();

    // Command-line
    int inputTextFlags = ImGuiInputTextFlags.EnterReturnsTrue;
    if (ImGui.inputText("Input", inputBuf, inputTextFlags)) {
      String s = inputBuf.toString().trim();
      if (!s.isEmpty() && inputHandler != null) {
        inputHandler.acceptInput(s);
        inputBuf.clear();
      } else {
        inputBuf.set("No listener set!");
      }
    }
  }

  private void clearLog() {
    items.clear();
  }
}
