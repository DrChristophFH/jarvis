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
  private EventSet waitingEventSet = null;
  
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
    threadList.setProvider(this::getThreads);
    toStringProcessor.start();
  }

  public void launch() {
    Thread debugThread = new Thread(this::startDebugging);
    debugThread.setDaemon(true);
    debugThread.setName("Jarvis Debugger");
    debugThread.start();
  }

  public void executeCommand(StepCommand command, int suspendPolicy) {
    this.processUserCommand(command, suspendPolicy);
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

  public List<ThreadReference> getThreads() {
    if (vm == null) {
      return null;
    }
    return vm.allThreads();
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
      this.threadList.start();
      objectGraphModel.clear();
      this.debug();
    } catch (VMDisconnectedException e) {
      logger.logInfo("VM disconnected");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void debug() throws InterruptedException, AbsentInformationException, IncompatibleThreadStateException, InvalidTypeException, ClassNotLoadedException, InvocationException {
    EventSet eventSet;

    while ((eventSet = vm.eventQueue().remove()) != null) {
      boolean resume = true;

      for (Event event : eventSet) {
        if (event instanceof ClassPrepareEvent e) {
          handleClassPrepareEvent(e);
        } else if (event instanceof BreakpointEvent || event instanceof StepEvent) {
          resume = false;
          handleBreakStepEvent(event);
        } else if (event instanceof ExceptionEvent exceptionEvent) {
          handleExceptionEvent(exceptionEvent);
        } else if (event instanceof VMDisconnectEvent vmDisconnectEvent) {
          handleVMDisconnectEvent(vmDisconnectEvent);
          return;
        } else {
          logger.logInfo(event.toString());
        }
      }

      if (resume) {
        eventSet.resume();
      } else {
        waitingEventSet = eventSet;
      }
    }
  }

  private void handleVMDisconnectEvent(VMDisconnectEvent vmDisconnectEvent) {
    logger.logInfo("VM disconnected");
    if(vmDisconnectEvent.virtualMachine().equals(vm)) {
      vm = null;
    }
  }

  private void handleExceptionEvent(ExceptionEvent exceptionEvent) {
    // For more detailed exceptions, we could implement a custom JObjectReference
    // for the toStringProcessor to pass the along with the exception object
    // the JObjectReference would then be able to print the ToString
    // result upon receiving it from the toStringProcessor
    logger.logWarning(exceptionEvent.toString());
  }

  private void handleBreakStepEvent(Event event) throws InterruptedException, IncompatibleThreadStateException {
    logger.logInfo(event.toString());

    if (toStringProcessor.isProcessing()) {
      toStringProcessor.stopProcessing();
      toStringProcessor.waitForStopSignal();
      toStringProcessor.clear();
    }

    currentThread = ((LocatableEvent) event).thread();

    objectGraphModel.syncWith(currentThread, toStringProcessor::addTask);
    callStackModel.syncWith(new ArrayList<>(currentThread.frames()));
    
    // kickoff toString
    toStringProcessor.signalToStart(currentThread);
  }

  private void handleClassPrepareEvent(ClassPrepareEvent e) throws AbsentInformationException {
    logger.logInfo(e.toString() + " for " + e.referenceType().name());
    classModel.addFromRefType(e.referenceType());
    this.setBreakPoints(e);
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

  private void processUserCommand(StepCommand command, int suspendPolicy) {
    if (currentThread == null) {
      logger.logWarning("No current thread");
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

    StepRequest stepRequest = null;

    switch (command) {
      case STEP_INTO -> {
        stepRequest = eventRequestManager.createStepRequest(currentThread, StepRequest.STEP_LINE, StepRequest.STEP_INTO);
      }
      case STEP_OVER -> {
        stepRequest = eventRequestManager.createStepRequest(currentThread, StepRequest.STEP_LINE, StepRequest.STEP_OVER);
      }
      case STEP_OUT -> {
        stepRequest = eventRequestManager.createStepRequest(currentThread, StepRequest.STEP_LINE, StepRequest.STEP_OUT);
      }
      case RESUME -> {
      }
      case STOP -> {
        stepRequest = eventRequestManager.createStepRequest(currentThread, StepRequest.STEP_LINE, StepRequest.STEP_INTO);
      }
      default -> throw new IllegalArgumentException("Unexpected value: " + command);
    }

    if (stepRequest != null) {
      stepRequest.addCountFilter(1);
      stepRequest.setSuspendPolicy(suspendPolicy);
      stepRequest.enable();
    }

    if (waitingEventSet != null) {
      waitingEventSet.resume();
      waitingEventSet = null;
    }
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
      bpReq.setSuspendPolicy(bp.getSuspendPolicy());
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
      bpReq.setSuspendPolicy(bp.getSuspendPolicy());
      bpReq.enable();
      bp.setRequest(bpReq);
    } catch (AbsentInformationException e) {
      e.printStackTrace();
      logger.logError("AbsentInformationException while setting breakpoint for " + bp.getClassName() + " at line " + bp.getLine());
    }
  }

  private void enableClassPrepareRequest() {
    ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
    classPrepareRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
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
