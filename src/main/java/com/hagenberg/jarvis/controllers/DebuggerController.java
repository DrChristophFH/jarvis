package com.hagenberg.jarvis.controllers;

import com.hagenberg.jarvis.debugger.JarvisDebuggerService;
import com.hagenberg.jarvis.debugger.StepCommand;
import com.hagenberg.jarvis.util.ServiceProvider;

public class DebuggerController {

    private final JarvisDebuggerService debuggerService;

    public DebuggerController(JarvisDebuggerService service) {
        this.debuggerService = service;
    }

    public void onStepIntoButtonClicked() {
        debuggerService.executeCommand(StepCommand.STEP_INTO);
    }

    public void onStepOverButtonClicked() {
        debuggerService.executeCommand(StepCommand.STEP_OVER);
    }
}