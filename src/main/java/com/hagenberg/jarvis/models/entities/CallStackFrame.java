package com.hagenberg.jarvis.models.entities;

import java.util.List;

public class CallStackFrame {
    private String className;
    private String methodName;
    private List<String> parameters;

    public CallStackFrame(String className, String methodName, List<String> parameters) {
        this.className = className;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
}