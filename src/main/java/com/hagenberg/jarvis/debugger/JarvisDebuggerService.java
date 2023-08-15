package com.hagenberg.jarvis.debugger;

import com.hagenberg.jarvis.models.CallStackModel;
import com.hagenberg.jarvis.models.entities.CallStackFrame;
import com.hagenberg.jarvis.models.entities.MethodParameter;
import com.hagenberg.jarvis.util.OutputHelper;
import com.hagenberg.jarvis.util.ServiceProvider;
import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("rawtypes")
public class JarvisDebuggerService {
    private final Class debugClass;
    private VirtualMachine vm;
    private int[] breakPointLines;

    private CompletableFuture<StepCommand> stepCommand;

    private final CallStackModel callStackModel = ServiceProvider.getInstance().getDependency(CallStackModel.class);

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
            this.updateCallStackModel(currentThread);
            this.displayVariables(currentThread);
            processUserCommand(currentThread, this.waitForUserCommand());
        }
        if (event instanceof ExceptionEvent exceptionEvent) {
            ObjectReference exceptionObj = exceptionEvent.exception();

            // Get the toString() method of the exception
            Method toStringMethod = exceptionObj.referenceType().methodsByName("toString").get(0);

            // Invoke the toString() method
            String exceptionAsString = exceptionObj.invokeMethod(exceptionEvent.thread(), toStringMethod, new ArrayList<>(), 0).toString();

            this.out.printError(exceptionAsString);
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

    }

    private void updateCallStackModel(ThreadReference thread) throws IncompatibleThreadStateException, AbsentInformationException, ClassNotLoadedException {
        List<StackFrame> frames = thread.frames();

        Platform.runLater(callStackModel::clear);

        for (StackFrame frame : frames) {
            Location location = frame.location();
            String className = location.declaringType().name();
            String methodName = location.method().name();
            List<MethodParameter> parameters = new ArrayList<>();

            // Fetching parameters. For simplicity, just getting the types. You can further enhance this.
            for (LocalVariable variable : frame.visibleVariables()) {
                if (variable.isArgument()) {
                    parameters.add(new MethodParameter(variable.typeName(), variable.name(), frame.getValue(variable).toString()));
                }
            }

            Platform.runLater(() -> callStackModel.add(new CallStackFrame(className, methodName, parameters, location.lineNumber())));
        }
    }

    private void setBreakPoints(ClassPrepareEvent event) throws AbsentInformationException {
        ClassType classType = (ClassType) event.referenceType();
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

    private void enableExceptionRequest() {
        ExceptionRequest exceptionRequest = vm.eventRequestManager().createExceptionRequest(null, true, true);
        exceptionRequest.enable();
    }

    private void connectAndLaunchVM() throws Exception {
        String projectDir = System.getProperty("user.dir");
        String classPath = projectDir + "/target/classes";
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
        arguments.get("main").setValue(debugClass.getName());
        arguments.get("options").setValue("-cp " + classPath);
        vm = launchingConnector.launch(arguments);
    }
}
