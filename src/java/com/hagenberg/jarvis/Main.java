package com.hagenberg.jarvis;

import com.hagenberg.imgui.Application;
import com.hagenberg.imgui.Configuration;
import com.hagenberg.jarvis.views.Jarvis;

import imgui.ImGui;
import imgui.type.ImBoolean;

public class Main extends Application {

  ImBoolean showMetricsWindow = new ImBoolean(false);
  Jarvis jarvis = new Jarvis(this);

  @Override
  protected void preRun() {
    super.preRun();
    
  }

  @Override
  public void process() {
    if (showMetricsWindow.get()) ImGui.showDemoWindow(showMetricsWindow);
    jarvis.render();
  }

  public static void main(String[] args) {
    launch(new Main(), new Configuration());
  }
}
