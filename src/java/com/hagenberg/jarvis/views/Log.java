package com.hagenberg.jarvis.views;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.util.Logger;
import com.hagenberg.jarvis.util.Logger.LogEntry;

import imgui.ImGui;
import imgui.ImGuiListClipper;
import imgui.ImGuiTextFilter;
import imgui.callback.ImListClipperCallback;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public class Log extends View {
  private Logger instance = Logger.getInstance();
  private ImGuiTextFilter filter = new ImGuiTextFilter();
  private ImBoolean autoScroll = new ImBoolean(true);

  public Log(String title) {
    setName(title);
  }

  @Override
  public void render() {
    ImGui.setNextWindowSize(500, 400, ImGuiCond.FirstUseEver);
    super.render();
  }

  @Override
  protected void renderWindow() {
    if (ImGui.beginPopup("Options")) {
      ImGui.checkbox("Auto-scroll", autoScroll);
      ImGui.endPopup();
    }

    if (ImGui.button("Options")) {
      ImGui.openPopup("Options");
    }

    ImGui.sameLine();
    filter.draw("Filter", -100.0f);

    ImGui.separator();

    if (ImGui.beginChild("scrolling", 0, 0, false, ImGuiWindowFlags.HorizontalScrollbar)) {
      ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

      List<LogEntry> buffer = instance.getBuffer();

      if (filter.isActive()) {
        buffer.forEach(entry -> {
          if (filter.passFilter(entry.fullMessage())) {
            printLogEntry(entry);
          }
        });
      } else { // display all
        ImGuiListClipper.forEach(buffer.size(), new ImListClipperCallback() {
          public void accept(int index) {
            printLogEntry(buffer.get(index));
          }
        });
      }

      ImGui.popStyleVar();

      if (autoScroll.get() && ImGui.getScrollY() >= ImGui.getScrollMaxY()) {
        ImGui.setScrollHereY(1.0f);
      }
    }
    ImGui.endChild();
  }

  private void printLogEntry(LogEntry entry) {
    ImGui.text("[" + entry.time() + "] ");
    ImGui.sameLine();
    switch (entry.level()) {
      case INFO -> ImGui.textColored(Colors.Info, "INFO ");
      case WARNING -> ImGui.textColored(Colors.Warning, "WARNING ");
      case ERROR -> ImGui.textColored(Colors.Error, "ERROR ");
    }
    ImGui.sameLine();
    ImGui.textUnformatted(entry.message());
  }
}