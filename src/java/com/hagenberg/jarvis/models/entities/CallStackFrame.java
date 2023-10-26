package com.hagenberg.jarvis.models.entities;

import java.util.List;

public class CallStackFrame {
    private String className;
    private String methodName;
    private List<MethodParameter> parameters;
    private int lineNumber;

    public CallStackFrame(String className, String methodName, List<MethodParameter> parameters, int lineNumber) {
        this.className = className;
        this.methodName = methodName;
        this.parameters = parameters;
        this.lineNumber = lineNumber;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<MethodParameter> getParameters() {
        return parameters;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}