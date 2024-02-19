package com.hagenberg.jarvis.views;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.hagenberg.imgui.Application;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.View;
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

  private ImString classPath = new ImString("target/classes/");
  private ImString mainClass = new ImString("com.hagenberg.debuggee.JDIExampleDebuggee");

  private InteractionState interactionState = new InteractionState();

  private List<View> views = new ArrayList<>();

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
    views.add(eventLog);
    views.add(console);
    views.add(classList);
    views.add(objectGraph);
    views.add(layouterControl);
    views.add(debugStepControl);
    views.add(breakPointControl);
    views.add(localVarList);
    views.add(objectList);
    views.add(callStack);
    views.add(linePreview);
    views.add(templateBuilder);
    breakPointControl.setClassPath(classPath.get());
  }

  public void render() {
    ImGui.setNextWindowSize(0, 0, ImGuiCond.FirstUseEver);
    ImGui.begin(name, ImGuiWindowFlags.MenuBar);
    ImGui.text("Welcome to Jarvis!");
    JarvisConfig();
    HelpSection();
    ConfigSection();
    MenuBar();
    ImGui.end();

    Profiler.show();

    for (View view : views) {
      if (view.getShowWindow()) {
        view.render();
      }
    }
  }

  private void MenuBar() {
    if (ImGui.beginMenuBar()) {
      if (ImGui.beginMenu("View")) {
        for (View view : views) {
          if (ImGui.menuItem(view.getName(), "", view.getShowWindow())) {
            view.setShowWindow(!view.getShowWindow());
          }
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
      jarvisDebugger.setClassPath(classPath.get());
      jarvisDebugger.setMainClass(mainClass.get());
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
      ImGui.bulletText("Sections below are demonstrating many aspects of the library."); //TODO
      ImGui.bulletText("The \"Examples\" menu above leads to more demo contents.");
      ImGui.separator();
      ImGui.showUserGuide();
    }
  }

  private void ConfigSection() {
    if (ImGui.collapsingHeader("Configuration")) {
      if(ImGui.checkbox("Resolve Critical Classes", objectGraph.getObjectGraphModel().isResolveSpecialClasses())) {
        objectGraph.getObjectGraphModel().setResolveSpecialClasses(!objectGraph.getObjectGraphModel().isResolveSpecialClasses());
      }
      ImGui.sameLine();
      Snippets.drawHelpMarker("Resolve critical classes that blow up the object graph, like java.lang.Class");
    }
  }
}
