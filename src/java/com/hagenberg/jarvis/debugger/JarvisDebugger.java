package com.hagenberg.jarvis.debugger;

import com.hagenberg.jarvis.models.CallStackModel;
import com.hagenberg.jarvis.models.ClassModel;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.BreakPoint;
import com.hagenberg.jarvis.util.Logger;
import com.hagenberg.jarvis.views.Log;
import com.hagenberg.jarvis.views.ThreadList;
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

  private final Logger logger = Logger.getInstance();
  private final DebugeeConsole debuggeeConsole;
  private final ThreadList threadList;
  private final BreakPointProvider breakPointProvider;

  // ------------------------------------------
  // ------------- Public Methods -------------
  // ------------------------------------------

  public JarvisDebugger(Log eventLog, BreakPointProvider breakPointProvider, DebugeeConsole debuggeeConsole, ThreadList threadList) {
    this.breakPointProvider = breakPointProvider;
    breakPointProvider.setBreakPointCreationCallback(this::connectNewBreakPoint);
    this.debuggeeConsole = debuggeeConsole;
    debuggeeConsole.registerInputHandler(this::handleInput);
    this.threadList = threadList;
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
    logger.logInfo("Starting debugger...");
    try {
      this.connectAndLaunchVM();
      this.enableClassPrepareRequest();
      this.enableExceptionRequest();
      this.startStreamThread();
      objectGraphModel.clear();
      this.debug();
    } catch (VMDisconnectedException e) {
      logger.logInfo("VM disconnected");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void debug() throws InterruptedException, AbsentInformationException, IncompatibleThreadStateException, InvalidTypeException,
      ClassNotLoadedException, InvocationException {
    EventSet eventSet;
    while ((eventSet = vm.eventQueue().remove()) != null) {
      threadList.updateThreads(vm.allThreads());
      for (Event event : eventSet) {
        if (event instanceof ClassPrepareEvent e) {
          logger.logInfo(event.toString() + " for " + e.referenceType().name());
          classModel.addFromRefType(e.referenceType());
          this.setBreakPoints(e);
          eventSet.resume();
        } else if (event instanceof BreakpointEvent || event instanceof StepEvent) {
          logger.logInfo(event.toString());
          if (toStringProcessor.isProcessing()) {
            toStringProcessor.stopProcessing();
            toStringProcessor.waitForStopSignal();
            toStringProcessor.clear();
          }

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
          logger.logWarning(exceptionEvent.toString());
          eventSet.resume();
        } else if (event instanceof VMDisconnectEvent) {
          logger.logInfo("VM disconnected");
          eventSet.resume();
          vm = null;
          return;
        } else {
          logger.logInfo(event.toString());
          eventSet.resume();
        }
      }
      threadList.updateThreads(vm.allThreads());
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

    logger.logInfo("Processing user command: " + command);

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
      var locations = refType.locationsOfLine(bp.getLine());
      if (locations.isEmpty()) {
        continue;
      }
      BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(locations.get(0));
      bpReq.enable();
      bp.setRequest(bpReq);
    }
  }

  private void connectNewBreakPoint(BreakPoint bp) {
    if (vm == null) { // VM not yet connected
      return;
    }
    List<ReferenceType> refTypes = vm.classesByName(bp.getClassName());
    if (refTypes.isEmpty()) {
      return;
    }
    ReferenceType refType = refTypes.get(0);
    List<Location> locations;
    try {
      locations = refType.locationsOfLine(bp.getLine());
      if (locations.isEmpty()) {
        return;
      }
      BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(locations.get(0));
      bpReq.enable();
      bp.setRequest(bpReq);
    } catch (AbsentInformationException e) {
      e.printStackTrace();
      logger.logError("AbsentInformationException while setting breakpoint for " + bp.getClassName() + " at line " + bp.getLine());
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
