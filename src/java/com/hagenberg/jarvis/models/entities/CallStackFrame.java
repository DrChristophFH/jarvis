package com.hagenberg.jarvis.models.entities;

import java.util.List;

import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.sun.jdi.StackFrame;

public class CallStackFrame {
  private StackFrame stackFrame;
  private String className;
  private String methodName;
  private int lineNumber;
  private List<LocalGVariable> parameters;

  public CallStackFrame(StackFrame frame, String className, String methodName, List<LocalGVariable> parameters, int lineNumber) {
    this.stackFrame = frame;
    this.className = className;
    this.methodName = methodName;
    this.parameters = parameters;
    this.lineNumber = lineNumber;
  }

  public StackFrame getStackFrame() {
    return stackFrame;
  }

  public String getClassName() {
    return className;
  }

  public String getMethodName() {
    return methodName;
  }

  public List<LocalGVariable> getParameters() {
    return parameters;
  }

  public int getLineNumber() {
    return lineNumber;
  }
}