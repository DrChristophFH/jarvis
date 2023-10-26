package com.hagenberg.jarvis.controllers;

import com.hagenberg.jarvis.debugger.JarvisDebugger;
import com.hagenberg.jarvis.debugger.StepCommand;

public class DebuggerController {

    private final JarvisDebugger debuggerService;

    public DebuggerController(JarvisDebugger service) {
        this.debuggerService = service;
    }

    public void onStepIntoButtonClicked() {
        debuggerService.executeCommand(StepCommand.STEP_INTO);
    }

    public void onStepOverButtonClicked() {
        debuggerService.executeCommand(StepCommand.STEP_OVER);
    }

    public void onStepOutButtonClicked() {
        debuggerService.executeCommand(StepCommand.STEP_OUT);
    }

    public void onResumeButtonClicked() {
        debuggerService.executeCommand(StepCommand.RESUME);
    }
}