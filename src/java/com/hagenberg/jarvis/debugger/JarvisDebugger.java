package com.hagenberg.jarvis.debugger;

import com.hagenberg.jarvis.models.CallStackModel;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.CallStackFrame;
import com.hagenberg.jarvis.models.entities.MethodParameter;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.StackFrameInformation;
import com.hagenberg.jarvis.views.Log;
import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class JarvisDebugger {
  private String classPath;
  private String mainClass;
  private VirtualMachine vm;

  private CompletableFuture<StepCommand> stepCommand;

  private CallStackModel callStackModel;
  private ObjectGraphModel objectGraphModel;

  private final Log eventLog;
  private final DebugeeConsole debuggeeConsole;
  private final BreakPointProvider breakPointProvider;

  // ------------------------------------------
  // ------------- Public Methods -------------
  // ------------------------------------------

  public JarvisDebugger(Log eventLog, BreakPointProvider breakPointProvider, DebugeeConsole debuggeeConsole) {
    this.eventLog = eventLog;
    this.breakPointProvider = breakPointProvider;
    this.debuggeeConsole = debuggeeConsole;
    debuggeeConsole.registerInputHandler(this::handleInput);
  }

  public void launch() {
    new Thread(this::startDebugging).start();
  }

  public void executeCommand(StepCommand command) {
    if (stepCommand != null && !stepCommand.isDone()) {
      stepCommand.complete(command);
    }
  }

  public String getClassPath() {
    return classPath;
  }

  public void setClassPath(String classPath) {
    this.classPath = classPath;
  }

  public String getMainClass() {
    return mainClass;
  }

  public void setMainClass(String mainClass) {
    this.mainClass = mainClass;
  }

  public void setObjectGraphModel(ObjectGraphModel objectGraphModel) {
    this.objectGraphModel = objectGraphModel;
  }

  public void setCallStackModel(CallStackModel callStackModel) {
    this.callStackModel = callStackModel;
  }

  // -------------------------------------------
  // ------------- Private Methods -------------
  // -------------------------------------------

  private void startDebugging() {
    eventLog.log("Starting debugger...");
    try {
      this.connectAndLaunchVM();
      this.enableClassPrepareRequest();
      this.enableExceptionRequest();
      this.startStreamThread();
      objectGraphModel.clear();
      this.debug();
    } catch (VMDisconnectedException e) {
      eventLog.log("VM disconnected");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void debug() throws InterruptedException, AbsentInformationException, IncompatibleThreadStateException,
      InvalidTypeException, ClassNotLoadedException, InvocationException {
    EventSet eventSet;
    while ((eventSet = vm.eventQueue().remove()) != null) {
      for (Event event : eventSet) {
        eventLog.log(event.toString());
        if (event instanceof ClassPrepareEvent e) {
          eventLog.log("Class prepared: " + e.referenceType().name());
          this.setBreakPoints(e);
        }
        if (event instanceof BreakpointEvent || event instanceof StepEvent) {
          ThreadReference currentThread = ((LocatableEvent) event).thread();
          this.updateObjectGraphModel(currentThread);
          callStackModel.syncWith(new ArrayList<>(currentThread.frames()), objectGraphModel);
          processUserCommand(currentThread, this.waitForUserCommand());
        }
        if (event instanceof ExceptionEvent exceptionEvent) {
          ObjectReference exceptionObj = exceptionEvent.exception();

          // Get the toString() method of the exception
          Method toStringMethod = exceptionObj.referenceType().methodsByName("toString").get(0);

          // Invoke the toString() method
          String exceptionAsString = exceptionObj.invokeMethod(exceptionEvent.thread(), toStringMethod, new ArrayList<>(), 0)
              .toString();

          eventLog.log(exceptionAsString);
        }
      }
      vm.resume(); // resume the thread after handling all events in the set
    }
  }

  private StepCommand waitForUserCommand() {
    stepCommand = new CompletableFuture<>();
    try {
      return stepCommand.get();
    } catch (InterruptedException | ExecutionException e) {
      return null;
    }
  }

  private void handleInput(String input) {
    try (OutputStream os = vm.process().getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {

      writer.write(input);
      writer.newLine();
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void startStreamThread() {
    new Thread(() -> {
      try {
        InputStream inStream = vm.process().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        String text;
        while ((text = reader.readLine()) != null) {
          debuggeeConsole.println(text);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }

  private void processUserCommand(ThreadReference currentThread, StepCommand command) {
    eventLog.log("Processing user command: " + command);

    EventRequestManager eventRequestManager = vm.eventRequestManager();
    // Cancel the last step request if it exists
    List<StepRequest> stepRequests = eventRequestManager.stepRequests();
    for (StepRequest stepRequest : stepRequests) {
      if (stepRequest.thread().equals(currentThread)) {
        eventRequestManager.deleteEventRequest(stepRequest);
      }
    }

    switch (command) {
    case STEP_INTO -> {
      StepRequest stepIntoRequest = eventRequestManager.createStepRequest(currentThread, StepRequest.STEP_LINE,
          StepRequest.STEP_INTO);
      stepIntoRequest.addCountFilter(1);
      stepIntoRequest.enable();
    }
    case STEP_OVER -> {
      StepRequest stepOverRequest = eventRequestManager.createStepRequest(currentThread, StepRequest.STEP_LINE,
          StepRequest.STEP_OVER);
      stepOverRequest.addCountFilter(1);
      stepOverRequest.enable();
    }
    case STEP_OUT -> {
      StepRequest stepOutRequest = eventRequestManager.createStepRequest(currentThread, StepRequest.STEP_LINE,
          StepRequest.STEP_OUT);
      stepOutRequest.addCountFilter(1);
      stepOutRequest.enable();
    }
    case RESUME -> {
    }
    case STOP -> {
      vm.exit(0);
    }
    default -> throw new IllegalArgumentException("Unexpected value: " + command);
    }
  }

  private void updateObjectGraphModel(ThreadReference thread)
      throws IncompatibleThreadStateException, AbsentInformationException, ClassNotLoadedException {
    // TODO clear root objects
    objectGraphModel.getLocalVars().clear();

    for (StackFrame frame : thread.frames()) {
      for (LocalVariable variable : frame.visibleVariables()) {
        String varName = variable.name();
        String staticType = variable.typeName();
        Value varValue = frame.getValue(variable);
        StackFrameInformation sfInfo = new StackFrameInformation();
        objectGraphModel.addLocalVariable(variable, varName, varValue, staticType, sfInfo);
      }
    }
  }

  private void setBreakPoints(ClassPrepareEvent event) throws AbsentInformationException {
    String className = event.referenceType().name();
    ReferenceType refType = event.referenceType();
    List<Integer> lines = breakPointProvider.getBreakPoints(className);
    if (lines == null) {
      return;
    }
    for (int lineNumber : lines) {
      Location location = refType.locationsOfLine(lineNumber).get(0);
      BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
      bpReq.enable();
    }
  }

  private void enableClassPrepareRequest() {
    ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
    classPrepareRequest.enable();
  }

  private void enableExceptionRequest() {
    ExceptionRequest exceptionRequest = vm.eventRequestManager().createExceptionRequest(null, true, true);
    exceptionRequest.enable();
  }

  private void connectAndLaunchVM() throws Exception {
    LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
    Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
    arguments.get("main").setValue(mainClass);
    arguments.get("options").setValue("-cp " + classPath);
    vm = launchingConnector.launch(arguments);
  }
}
