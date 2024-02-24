package com.hagenberg.jarvis.debugger;

import com.hagenberg.jarvis.models.CallStackModel;
import com.hagenberg.jarvis.models.ClassModel;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.BreakPoint;
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

public class JarvisDebugger {
  private String classPath;
  private String mainClass;
  private VirtualMachine vm;

  private ThreadReference currentThread;

  private CallStackModel callStackModel;
  private ObjectGraphModel objectGraphModel;
  private ClassModel classModel;

  private ToStringProcessor toStringProcessor = new ToStringProcessor();

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
    toStringProcessor.start();
  }

  public void launch() {
    Thread debugThread = new Thread(this::startDebugging);
    debugThread.setDaemon(true);
    debugThread.setName("Jarvis Debugger");
    debugThread.start();
  }

  public void executeCommand(StepCommand command) {
    this.processUserCommand(command);
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

  public void setClassModel(ClassModel classModel) {
    this.classModel = classModel;
  }

  public void shutdown() {
    if (vm != null) vm.exit(0);
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

  private void debug() throws InterruptedException, AbsentInformationException, IncompatibleThreadStateException, InvalidTypeException,
      ClassNotLoadedException, InvocationException {
    EventSet eventSet;
    while ((eventSet = vm.eventQueue().remove()) != null) {
      for (Event event : eventSet) {
        System.out.println("Event: " + event);
        eventLog.log(event.toString());
        if (event instanceof ClassPrepareEvent e) {
          eventLog.log("Class prepared: " + e.referenceType().name());
          classModel.addFromRefType(e.referenceType());
          this.setBreakPoints(e);
          eventSet.resume();
        } else if (event instanceof BreakpointEvent || event instanceof StepEvent) {
          if (toStringProcessor.isProcessing()) {
            toStringProcessor.stopProcessing();
            toStringProcessor.waitForStopSignal();
            toStringProcessor.clear();
          }

          System.out.println("Breakpoint reached");

          ThreadReference currentThread = ((LocatableEvent) event).thread();
          this.currentThread = currentThread;

          objectGraphModel.syncWith(currentThread, toStringProcessor::addTask);
          callStackModel.syncWith(new ArrayList<>(currentThread.frames()));
          
          // kickoff toString
          toStringProcessor.signalToStart(currentThread);
        } else if (event instanceof ExceptionEvent exceptionEvent) {
          // For more detailed exceptions, we could implement a custom JObjectReference
          // for the toStringProcessor to pass the along with the exception object
          // the JObjectReference would then be able to print the ToString
          // result upon receiving it from the toStringProcessor
          eventLog.log(exceptionEvent.toString());
          eventSet.resume();
        } else if (event instanceof VMDisconnectEvent) {
          eventLog.log("VM disconnected");
          eventSet.resume();
          vm = null;
          return;
        } else {
          eventSet.resume();
        }
        System.out.println("Event Handled");
      }
    }
  }

  private void handleInput(String input) {
    try (OutputStream os = vm.process().getOutputStream(); BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
      writer.write(input);
      writer.newLine();
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void startStreamThread() {
    Thread streamThread = new Thread(() -> {
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
    });

    streamThread.setDaemon(true);
    streamThread.setName("Jarvis In/Output Stream Thread");
    streamThread.start();
  }

  private void processUserCommand(StepCommand command) {
    if (currentThread == null) {
      return;
    } 
    System.out.println(command);

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
        StepRequest stepIntoRequest = eventRequestManager.createStepRequest(currentThread, StepRequest.STEP_LINE, StepRequest.STEP_INTO);
        stepIntoRequest.addCountFilter(1);
        stepIntoRequest.enable();
      }
      case STEP_OVER -> {
        StepRequest stepOverRequest = eventRequestManager.createStepRequest(currentThread, StepRequest.STEP_LINE, StepRequest.STEP_OVER);
        stepOverRequest.addCountFilter(1);
        stepOverRequest.enable();
      }
      case STEP_OUT -> {
        StepRequest stepOutRequest = eventRequestManager.createStepRequest(currentThread, StepRequest.STEP_LINE, StepRequest.STEP_OUT);
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
    vm.resume();
  }

  private void setBreakPoints(ClassPrepareEvent event) throws AbsentInformationException {
    String className = event.referenceType().name();
    ReferenceType refType = event.referenceType();
    List<BreakPoint> breakpoints = breakPointProvider.getBreakPoints(className);
    if (breakpoints == null) {
      return;
    }
    for (BreakPoint bp : breakpoints) {
      Location location = refType.locationsOfLine(bp.getLine()).get(0); // todo check if there are locations
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
