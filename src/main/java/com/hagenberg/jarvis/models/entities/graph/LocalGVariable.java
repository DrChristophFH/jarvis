package com.hagenberg.jarvis.models.entities.graph;

public class LocalGVariable extends GVariable {
    private final StackFrameInformation stackFrameInformation; // A class to store stack frame information

    public LocalGVariable(String name, GNode node, StackFrameInformation stackFrameInformation) {
        super(name, node);
        this.stackFrameInformation = stackFrameInformation;
    }

    public StackFrameInformation getStackFrameInformation() {
        return stackFrameInformation;
    }
}