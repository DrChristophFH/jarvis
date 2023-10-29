package com.hagenberg.jarvis.views;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.hagenberg.imgui.View;

import imgui.ImGui;
import imgui.ImGuiListClipper;
import imgui.ImGuiTextFilter;
import imgui.callback.ImListClipperCallback;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public class Log extends View {
  private List<String> buffer = new ArrayList<>();
  private ImGuiTextFilter filter = new ImGuiTextFilter();
  private ImBoolean autoScroll = new ImBoolean(true);

  public Log(String title) {
    setName(title);
  }

  public void log(String text, Object... args) {
    String formattedText = String.format(text, args);
    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    buffer.add("[" + time + "] " + formattedText);
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

      if (filter.isActive()) {
        buffer.forEach(line -> {
          if (filter.passFilter(line)) {
            ImGui.textUnformatted(line);
          }
        });
      } else { // display all
        ImGuiListClipper.forEach(buffer.size(), new ImListClipperCallback() {
          public void accept(int index) {
            ImGui.textUnformatted(buffer.get(index));
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
}