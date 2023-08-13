package com.hagenberg.jarvis.debugger;

import com.hagenberg.jarvis.util.OutputHelper;
import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("rawtypes")
public class JarvisDebuggerService {
    private final Class debugClass;
    private VirtualMachine vm;
    private int[] breakPointLines;

    private CompletableFuture<StepCommand> stepCommand;

    private final OutputHelper out = new OutputHelper();

    /**
     * @param debugClass the class to be debugged
     */
    public JarvisDebuggerService(Class debugClass) {
        this.debugClass = debugClass;
    }

    /**
     * @param breakPointLines the lines where the debugger should stop
     */
    public void setBreakPointLines(int[] breakPointLines) {
        this.breakPointLines = breakPointLines;
    }

    /**
     * Launches the debugger as a new thread
     */
    public void launch() {
        new Thread(this::startDebugging).start();
    }

    /**
     * @param command the command to be executed
     */
    public void executeCommand(StepCommand command) {
        if (stepCommand != null && !stepCommand.isDone()) {
            stepCommand.complete(command);
        }
    }

    private void startDebugging() {
        this.out.printInfo("Starting debugger...");
        try {
            this.connectAndLaunchVM();
            this.enableClassPrepareRequest();
            this.enableExceptionRequest();
            this.startStreamThread();
            this.debug();
        } catch (VMDisconnectedException e) {
            this.out.printInfo("VM disconnected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void debug() throws InterruptedException, AbsentInformationException, IncompatibleThreadStateException, InvalidTypeException, ClassNotLoadedException, InvocationException {
        EventSet eventSet;
        while ((eventSet = vm.eventQueue().remove()) != null) {
            for (Event event : eventSet) {
                this.handleEvent(event);
            }
            vm.resume(); // resume the thread after handling all events in the set
        }
    }

    private void handleEvent(Event event) throws AbsentInformationException, IncompatibleThreadStateException, InvalidTypeException, ClassNotLoadedException, InvocationException {
        this.out.printEvent(event.toString());
        if (event instanceof ClassPrepareEvent) {
            this.setBreakPoints((ClassPrepareEvent) event);
        }
        if (event instanceof BreakpointEvent || event instanceof StepEvent) {
            ThreadReference currentThread = ((LocatableEvent) event).thread();
            this.displayCallStack(currentThread);
            this.displayVariables(currentThread);
            processUserCommand(currentThread, this.waitForUserCommand());
        }
        if (event instanceof ExceptionEvent exceptionEvent) {
            ObjectReference exceptionObj = exceptionEvent.exception();

            // Get the toString() method of the exception
            Method toStringMethod = exceptionObj.referenceType().methodsByName("toString").get(0);

            // Invoke the toString() method
            String exceptionAsString = exceptionObj.invokeMethod(exceptionEvent.thread(), toStringMethod, new ArrayList<>(), 0).toString();
        }
    }

    private StepCommand waitForUserCommand() {
        stepCommand = new CompletableFuture<>();
        try {
            return stepCommand.get();
        } catch (InterruptedException | ExecutionException e) {
            // handle the exception
            return null; // or handle appropriately
        }
    }

    private void enableExceptionRequest() {
        ExceptionRequest exceptionRequest = vm.eventRequestManager().createExceptionRequest(null, true, true);
        exceptionRequest.enable();
    }

    private void startStreamThread() {
        new Thread(() -> {
            try {
                InputStream inStream = vm.process().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
                String text;
                while ((text = reader.readLine()) != null) {
                    this.out.printDebuggee(text);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void processUserCommand(ThreadReference currentThread, StepCommand command) {
        this.out.printUserInput("Processing user command: " + command);

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
        }
    }

    private void displayVariables(ThreadReference thread) throws IncompatibleThreadStateException, AbsentInformationException {
        StackFrame stackFrame = thread.frame(0);
        Map<LocalVariable, Value> visibleVariables = stackFrame.getValues(stackFrame.visibleVariables());
        for (Map.Entry<LocalVariable, Value> entry : visibleVariables.entrySet()) {
            Stack<ObjectReference > visitedObjects = new Stack<>();
            printObject(entry.getKey().name(), entry.getValue(), 1, visitedObjects);
        }
    }

    private void printObject(String name, Value value, int i, Stack<ObjectReference> visitedObjects) {
        if (i > 25) {
//            this.out.printInfo("(max depth reached)");
            return;
        }
        String padding = "    ".repeat(i);
        if (value == null) {
//            this.out.printDebug(padding + name + " = null");
            return;
        }
//        this.out.printDebug(padding + this.out.colourString(value.type().name(), OutputHelper.ANSI_BLUE) + " " + name + " = " + value);
        if (value instanceof ObjectReference) {
            if (visitedObjects.contains((ObjectReference) value)) {
//                this.out.printDebug(padding + "    (already visited)");
                return;
            }
            ReferenceType type = ((ObjectReference) value).referenceType();
            visitedObjects.push((ObjectReference) value);
            for (Field field : type.fields()) {
                if (Modifier.isStatic(field.modifiers())) {
                    continue;
                }
                printObject(Modifier.toString(field.modifiers()) + "(" + field.modifiers() + ") " + field.name(), ((ObjectReference) value).getValue(field), i + 1, visitedObjects);
            }
            if (type instanceof ArrayType) {
                int index = 0;
                for (Value arrayElement : ((ArrayReference) value).getValues()) {
                    printObject(name+"["+index+"]", arrayElement, i + 1, visitedObjects);
                    index++;
                }
            }
            visitedObjects.pop();
        }
    }

    private void displayCallStack(ThreadReference thread) throws IncompatibleThreadStateException {
        List<StackFrame> stackFrames = thread.frames();
//        this.out.printCallStack(" ==== Call Stack ==== ");
        for (int i = stackFrames.size() - 1; i >= 0; i--) {
            StackFrame frame = stackFrames.get(i);
            String padding = i < stackFrames.size() - 1 ? "  ".repeat(stackFrames.size() - i - 1) + "└ " : "";
//            this.out.printCallStack(padding + this.out.colourString(frame.location().method().name() + "()", OutputHelper.ANSI_PURPLE) + " at " + frame.location().toString());
        }
    }

    private void setBreakPoints(ClassPrepareEvent event) throws AbsentInformationException {
        ClassType classType = (ClassType) event.referenceType();
//        this.out.printInfo("Setting breakpoints in " + classType.name());
        for (int lineNumber : breakPointLines) {
            Location location = classType.locationsOfLine(lineNumber).get(0);
            BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
            bpReq.enable();
        }
    }

    private void enableClassPrepareRequest() {
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(debugClass.getName());
        classPrepareRequest.enable();
    }

    private void connectAndLaunchVM() throws Exception {
        String projectDir = System.getProperty("user.dir");
        String classPath = projectDir + "/out/production/JDI-Test";
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
        arguments.get("main").setValue(debugClass.getName());
        arguments.get("options").setValue("-cp " + classPath);
        vm = launchingConnector.launch(arguments);
    }
}
