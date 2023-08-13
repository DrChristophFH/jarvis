package com.hagenberg.jarvis.models;

import com.hagenberg.jarvis.models.entities.CallStackFrame;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CallStackModel {
    private ObservableList<CallStackFrame> callStack = FXCollections.observableArrayList();

    public void add(CallStackFrame frame) {
        callStack.add(frame);
    }

    public ObservableList<CallStackFrame> getCallStack() {
        return callStack;
    }

    public void clear() {
        callStack.clear();
    }
}
