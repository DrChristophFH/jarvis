package com.hagenberg.jarvis.views;

import org.lwjgl.glfw.GLFW;

import com.hagenberg.imgui.Application;
import com.hagenberg.jarvis.debugger.JarvisDebugger;
import com.hagenberg.jarvis.models.InteractionState;
import com.hagenberg.jarvis.util.Profiler;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

public class Jarvis {
  private final String name = "Jarvis Control Panel";

  private final Application application;

  private ImString classPath = new ImString();
  private ImString mainClass = new ImString();

  private InteractionState interactionState = new InteractionState();

  private Log eventLog = new Log("Event Log");
  private Console console = new Console();
  private ClassList classList = new ClassList(interactionState);
  private ObjectGraph objectGraph = new ObjectGraph(classList.getModel());
  private LayouterControl layouterControl = new LayouterControl(objectGraph.getLayouter());
  private DebugStepControl debugStepControl = new DebugStepControl();
  private BreakPointControl breakPointControl = new BreakPointControl();
  private LocalVarList localVarList = new LocalVarList(interactionState);
  private ObjectList objectList = new ObjectList(interactionState);
  private CallStack callStack = new CallStack(objectGraph.getObjectGraphModel(), classList.getModel());
  private LinePreview linePreview = new LinePreview(callStack.getCallStackModel());
  private TemplateBuilder templateBuilder = new TemplateBuilder(objectGraph.getGraphTransformer().getRegistry());

  private JarvisDebugger jarvisDebugger = new JarvisDebugger(eventLog, breakPointControl, console);

  public Jarvis(Application application) {
    this.application = application;
  }

  public void render() {
    ImGui.setNextWindowSize(0, 0, ImGuiCond.FirstUseEver);
    ImGui.begin(name, ImGuiWindowFlags.MenuBar);
    ImGui.text("Welcome to Jarvis!");
    JarvisConfig();
    HelpSection();
    MenuBar();
    ImGui.end();

    Profiler.show();

    Profiler.start("objectGraph");
    if (objectGraph.getShowWindow()) objectGraph.render();
    Profiler.stop("objectGraph");
    Profiler.start("layouterControl");
    if (layouterControl.getShowWindow()) layouterControl.render();
    Profiler.stop("layouterControl");
    Profiler.start("templateBuilder");
    if (templateBuilder.getShowWindow()) templateBuilder.render();
    Profiler.stop("templateBuilder");
    Profiler.start("debugStepControl");
    if (debugStepControl.getShowWindow()) debugStepControl.render();
    Profiler.stop("debugStepControl");
    Profiler.start("breakPointControl");
    if (breakPointControl.getShowWindow()) breakPointControl.render();
    Profiler.stop("breakPointControl");
    Profiler.start("objectList");
    if (objectList.getShowWindow()) objectList.render();
    Profiler.stop("objectList");
    Profiler.start("localVarList");
    if (localVarList.getShowWindow()) localVarList.render();
    Profiler.stop("localVarList");
    Profiler.start("classList");
    if (classList.getShowWindow()) classList.render();
    Profiler.stop("classList");
    Profiler.start("callStack");
    if (callStack.getShowWindow()) callStack.render();
    Profiler.stop("callStack");
    Profiler.start("linePreview");
    if (linePreview.getShowWindow()) linePreview.render();
    Profiler.stop("linePreview");
    Profiler.start("eventLog");
    if (eventLog.getShowWindow()) eventLog.render();
    Profiler.stop("eventLog");
    Profiler.start("console");
    if (console.getShowWindow()) console.render();
    Profiler.stop("console");
  }

  private void MenuBar() {
    if (ImGui.beginMenuBar()) {
      if (ImGui.beginMenu("View")) {
        if (ImGui.menuItem("Object Graph", "", objectGraph.getShowWindow())) {
          objectGraph.setShowWindow(!objectGraph.getShowWindow());
        }
        if (ImGui.menuItem("Layouter Control", "", layouterControl.getShowWindow())) {
          layouterControl.setShowWindow(!layouterControl.getShowWindow());
        }
        if (ImGui.menuItem("Debug Step Control", "", debugStepControl.getShowWindow())) {
          debugStepControl.setShowWindow(!debugStepControl.getShowWindow());
        }
        if (ImGui.menuItem("Break Point Control", "", breakPointControl.getShowWindow())) {
          breakPointControl.setShowWindow(!breakPointControl.getShowWindow());
        }
        if (ImGui.menuItem("Object List", "", objectList.getShowWindow())) {
          objectList.setShowWindow(!objectList.getShowWindow());
        }
        if (ImGui.menuItem("Local Variable List", "", localVarList.getShowWindow())) {
          localVarList.setShowWindow(!localVarList.getShowWindow());
        }
        if (ImGui.menuItem("Class List", "", classList.getShowWindow())) {
          classList.setShowWindow(!classList.getShowWindow());
        }
        if (ImGui.menuItem("Call Stack", "", callStack.getShowWindow())) {
          callStack.setShowWindow(!callStack.getShowWindow());
        }
        if (ImGui.menuItem("Line Preview", "", linePreview.getShowWindow())) {
          linePreview.setShowWindow(!linePreview.getShowWindow());
        }
        if (ImGui.menuItem("Event Log", "", eventLog.getShowWindow())) {
          eventLog.setShowWindow(!eventLog.getShowWindow());
        }
        if (ImGui.menuItem("Console", "", console.getShowWindow())) {
          console.setShowWindow(!console.getShowWindow());
        }
        ImGui.endMenu();
      }
      ImGui.endMenuBar();
    }
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
      jarvisDebugger.setClassModel(classList.getModel());
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
