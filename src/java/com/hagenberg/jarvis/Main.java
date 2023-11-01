package com.hagenberg.jarvis;

import com.hagenberg.imgui.Application;
import com.hagenberg.imgui.Configuration;
import com.hagenberg.jarvis.views.Jarvis;

import imgui.ImGui;
import imgui.type.ImBoolean;

public class Main extends Application {

  ImBoolean showDemoWindow = new ImBoolean(true);
  Jarvis jarvis = new Jarvis(this);

  @Override
  protected void preRun() {
    super.preRun();
    
  }

  @Override
  public void process() {
    if (showDemoWindow.get()) ImGui.showDemoWindow(showDemoWindow);
    jarvis.render();
  }

  public static void main(String[] args) {
    launch(new Main(), new Configuration());
  }
}
