package com.hagenberg.jarvis.views;

import org.lwjgl.glfw.GLFW;

import com.hagenberg.imgui.Application;
import com.hagenberg.jarvis.debugger.JarvisDebugger;
import com.hagenberg.jarvis.models.InteractionState;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.type.ImString;

public class Jarvis {
  private final String name = "Jarvis Control Panel";

  private final Application application;

  private ImString classPath = new ImString();
  private ImString mainClass = new ImString();

  private InteractionState interactionState = new InteractionState();

  private Log eventLog = new Log("Event Log");
  private Console console = new Console();
  private ObjectGraph objectGraph = new ObjectGraph();
  private LayouterControl layouterControl = new LayouterControl(objectGraph.getLayouter());
  private DebugStepControl debugStepControl = new DebugStepControl();
  private BreakPointControl breakPointControl = new BreakPointControl();
  private LocalVarList localVarList = new LocalVarList(interactionState);
  private ObjectList objectList = new ObjectList(interactionState);
  private CallStack callStack = new CallStack();

  private JarvisDebugger jarvisDebugger = new JarvisDebugger(eventLog, breakPointControl, console);

  public Jarvis(Application application) {
    this.application = application;
  }

  public void render() {
    ImGui.setNextWindowSize(0, 0, ImGuiCond.FirstUseEver);
    ImGui.begin(name);
    ImGui.text("Welcome to Jarvis!");
    JarvisConfig();
    HelpSection();
    ImGui.end();

    if (objectGraph.getShowWindow()) objectGraph.render();
    if (layouterControl.getShowWindow()) layouterControl.render();
    if (debugStepControl.getShowWindow()) debugStepControl.render();
    if (breakPointControl.getShowWindow()) breakPointControl.render();
    if (objectList.getShowWindow()) objectList.render();
    if (localVarList.getShowWindow()) localVarList.render();
    if (callStack.getShowWindow()) callStack.render();
    if (eventLog.getShowWindow()) eventLog.render();
    if (console.getShowWindow()) console.render();
  }

  private void JarvisConfig() {
    if(ImGui.inputText("Class Path", classPath)) {
      breakPointControl.setClassPath(classPath.get());
    }
    ImGui.inputText("Main Class", mainClass);
    if (ImGui.button("Launch")) {
      // jarvisDebugger.setClassPath(classPath.get()); TODO
      // jarvisDebugger.setMainClass(mainClass.get());
      jarvisDebugger.setClassPath("target/classes/");
      jarvisDebugger.setMainClass("com.hagenberg.debuggee.JDIExampleDebuggee");
      jarvisDebugger.setObjectGraphModel(objectGraph.getObjectGraphModel());
      jarvisDebugger.setCallStackModel(callStack.getCallStackModel());
      objectList.setObjectGraphModel(objectGraph.getObjectGraphModel());
      localVarList.setObjectGraphModel(objectGraph.getObjectGraphModel());
      jarvisDebugger.launch();
      debugStepControl.setCommandExecutor(jarvisDebugger::executeCommand);
    }
    ImGui.sameLine();
    if (ImGui.button("Shutdown")) {
      jarvisDebugger.shutdown();
      GLFW.glfwSetWindowShouldClose(application.getHandle(), true);
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
