package com.hagenberg.jarvis.views;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.hagenberg.imgui.Application;
import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.debugger.JarvisDebugger;
import com.hagenberg.jarvis.models.InteractionState;
import com.hagenberg.jarvis.util.Profiler;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

public class Jarvis {
  private final String name = "Jarvis Control Panel";

  private final Application application;

  private ImString classPath = new ImString("target/classes/", 255);
  private ImString mainClass = new ImString("com.hagenberg.debuggee.JDIExampleDebuggee", 255);

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
    ImGui.sameLine();
    Snippets.drawHelpMarker("The class path to use for the debugger. This is the path to the compiled classes of the project.");

    ImGui.inputText("Main Class", mainClass); 
    ImGui.sameLine();
    Snippets.drawHelpMarker("The name of the main class to launch.");

    ImGui.inputText("src.zip Path", linePreview.getSrcZipPath());
    ImGui.sameLine();
    Snippets.drawHelpMarker("The path to the src.zip file of the JDK. This is fetched from the JAVA_HOME environment variable by default. If your JDK does not contain a src.zip file, you can download it from the Oracle website.");

    ImGui.text("Source Paths");
    ImGui.sameLine();
    Snippets.drawHelpMarker("The path to the source files of the project (user code).");
    int i = 0;
    for (ImString sourcePath : linePreview.getSourcePaths()) {
      ImGui.inputText("##" + i, sourcePath);
      i++;
    }
    if (ImGui.button("Add Source Path")) {
      linePreview.getSourcePaths().add(new ImString());
    }
    ImGui.sameLine();
    if (ImGui.button("Remove Source Path")) {
      if (!linePreview.getSourcePaths().isEmpty()) {
        linePreview.getSourcePaths().remove(linePreview.getSourcePaths().size() - 1);
      }
    }

    ImGui.pushStyleColor(ImGuiCol.Button, Colors.Success);

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
    ImGui.pushStyleColor(ImGuiCol.Button, Colors.Danger);

    if (ImGui.button("Shutdown")) {
      jarvisDebugger.shutdown();
      GLFW.glfwSetWindowShouldClose(application.getHandle(), true);
    }

    ImGui.popStyleColor(2);
  }

  private void HelpSection() {
    if (ImGui.collapsingHeader("Help")) {
      if (ImGui.treeNode("About")) {
        ImGui.textWrapped("Jarvis is a tool for debugging Java applications using the Java Debug Interface (JDI).");
        ImGui.treePop();
      }
      if (ImGui.treeNode("First Steps")) {
        ImGui.bullet(); 
        ImGui.textWrapped("Set the class path to point to the compiled classes of the project.");
        ImGui.bullet();
        ImGui.textWrapped("Set the main class name to the main class of the project.");
        ImGui.bullet();
        ImGui.textWrapped("Set the src.zip path to the src.zip file of the JDK.");
        ImGui.bullet();
        ImGui.textWrapped("Add the source paths of the project.");
        ImGui.bullet();
        ImGui.textWrapped("Navigate to the 'Breakpoint Control' dock and set breakpoints at the desired locations.");
        ImGui.bullet();
        ImGui.textWrapped("Click the 'Launch' button to start the debugging session.");
        ImGui.treePop();
      }
      if (ImGui.treeNode("General")) {
        ImGui.bullet();
        ImGui.textWrapped("Right-clicking a type allows to filter either in the object list or inspect the class in the class list.");
        ImGui.bullet();
        ImGui.textWrapped("Hovering over a highlighted type displays the fully qualified name of the type.");
        ImGui.bullet();
        ImGui.textWrapped("Closed docks can be opened using the 'View' menu in the 'Jarvis Control Panel' dock.");
        ImGui.treePop();
      }
      if (ImGui.treeNode("Breakpoint Control")) {
        ImGui.bullet();
        ImGui.textWrapped("The breakpoint control allows to set breakpoints at the desired locations in the source code.");
        ImGui.bullet();
        ImGui.textWrapped("Select the desired class to set a breakpoint in and enter the line number in the input field.");
        ImGui.bullet();
        ImGui.textWrapped("Breakpoints can be enabled, disabled and removed using the buttons in the breakpoint list.");
        ImGui.bullet();
        ImGui.textWrapped("Breakpoints are saved to a file and are restored on the next launch of the tool.");
        ImGui.treePop();
      }
      if (ImGui.treeNode("Object Graph")) {
        ImGui.bullet();
        ImGui.textWrapped("The object graph shows the objects in the heap and their references.");
        ImGui.bullet();
        ImGui.textWrapped("Right-clicking on the titlebar of an object brings up the objects context menu.");
        ImGui.bullet();
        ImGui.textWrapped("Selecting multiple objects by holding the left mouse button and dragging a selection rectangle around the objects and then right-clicking in an empty area, allows to freeze and unfreeze the selected objects. Frozen objects are highlighted in blue and are not layouted automatically.");
        ImGui.bullet();
        ImGui.textWrapped("The 'Layouter Control' dock allows to change layouting parameters of the object graph.");
        ImGui.treePop();
      }
      if (ImGui.treeNode("Object List")) {
        ImGui.bullet();
        ImGui.textWrapped("The object list shows the objects in the heap.");
        ImGui.bullet();
        ImGui.textWrapped("It acts as a top level view, showing all objects no matter their depth in the object graph.");
        ImGui.bullet();
        ImGui.textWrapped("For a more traditional view, use the local variable list.");
        ImGui.bullet();
        ImGui.textWrapped("Using the input field at the top, the object list can be filtered by the class name of the objects. Tab-completion is supported. (input must match fully!)");
        ImGui.treePop();
      }
      if (ImGui.treeNode("Local Variable List")) {
        ImGui.bullet();
        ImGui.textWrapped("The local variable list shows the local variables of the current frame in the call stack.");
        ImGui.bullet();
        ImGui.textWrapped("It acts as a traditional view, showing the local variables of the current frame.");
        ImGui.treePop();
      }
      if (ImGui.treeNode("Class List")) {
        ImGui.bullet();
        ImGui.textWrapped("The class list shows the classes prepared by the application.");
        ImGui.treePop();
      }
      if (ImGui.treeNode("Call Stack")) {
        ImGui.bullet();
        ImGui.textWrapped("The call stack shows the current call stack of the application in the current stopped thread.");
        ImGui.treePop();
      }
      if (ImGui.treeNode("Line Preview")) {
        ImGui.bullet();
        ImGui.textWrapped("The line preview shows the source code of the current line of the current frame in the call stack. For this to work properly, the src.zip file of the JDK and the source paths of the user code must be set.");
        ImGui.treePop();
      }
      if (ImGui.treeNode("Console")) {
        ImGui.bullet();
        ImGui.textWrapped("The console shows the output of the debuggee.");
        ImGui.treePop();
      }
      if (ImGui.treeNode("Event Log")) {
        ImGui.bullet();
        ImGui.textWrapped("The event log shows the events and other log information that occurs during usage of the tool.");
        ImGui.bullet();
        ImGui.textWrapped("The event log can be filtered by typing in the input field at the top.");
        ImGui.treePop();
      }
      if (ImGui.treeNode("ImGui User Guide")) {
        ImGui.showUserGuide();
        ImGui.treePop();
      }
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
