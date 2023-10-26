package com.hagenberg.jarvis.models;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.jarvis.models.entities.CallStackFrame;

public class CallStackModel {
    private List<CallStackFrame> callStack = new ArrayList<>();

    public void add(CallStackFrame frame) {
        callStack.add(frame);
    }

    public List<CallStackFrame> getCallStack() {
        return callStack;
    }

    public void clear() {
        callStack.clear();
    }
}
