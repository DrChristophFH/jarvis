package com.hagenberg.jarvis.views;

import com.hagenberg.jarvis.debugger.JarvisDebugger;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.type.ImString;

public class Jarvis {
  private final String name = "Jarvis Control Panel";

  private ImString classPath = new ImString();
  private ImString mainClass = new ImString();

  Log eventLog = new Log("Event Log");
  Console console = new Console();
  ObjectGraph objectGraph = new ObjectGraph();
  DebugStepControl debugStepControl = new DebugStepControl();
  BreakPointControl breakPointControl = new BreakPointControl();

  JarvisDebugger jarvisDebugger = new JarvisDebugger(eventLog, breakPointControl, console);

  public void render() {
    ImGui.setNextWindowSize(0, 0, ImGuiCond.FirstUseEver);
    ImGui.begin(name);
    ImGui.text("Welcome to Jarvis!");
    JarvisConfig();
    HelpSection();
    ImGui.end();

    if (objectGraph.getShowWindow()) objectGraph.render();
    if (debugStepControl.getShowWindow()) debugStepControl.render();
    if (breakPointControl.getShowWindow()) breakPointControl.render();
    if (eventLog.getShowWindow()) eventLog.render();
    if (console.getShowWindow()) console.render();
  }

  private void JarvisConfig() {
    if(ImGui.inputText("Class Path", classPath)) {
      breakPointControl.setClassPath(classPath.get());
    }
    ImGui.inputText("Main Class", mainClass);
    if (ImGui.button("Launch")) {
      jarvisDebugger.setClassPath(classPath.get());
      jarvisDebugger.setMainClass(mainClass.get());
      jarvisDebugger.launch();
      debugStepControl.setCommandExecutor(jarvisDebugger::executeCommand);
    }
  }

  private void HelpSection() {
    if (ImGui.collapsingHeader("Help")) {
      ImGui.separator();
      ImGui.bulletText("Sections below are demonstrating many aspects of the library.");
      ImGui.bulletText("The \"Examples\" menu above leads to more demo contents.");
      ImGui.separator();
      ImGui.showUserGuide();
    }
  }
}
