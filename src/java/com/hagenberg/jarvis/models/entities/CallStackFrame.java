package com.hagenberg.jarvis.models.entities;

import java.util.List;

import com.hagenberg.jarvis.models.entities.wrappers.JLocalVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JMethod;
import com.hagenberg.jarvis.models.entities.wrappers.JType;
import com.sun.jdi.StackFrame;

public class CallStackFrame {
  private StackFrame stackFrame;
  private JType classType;
  private JMethod method;
  private int lineNumber;
  private List<JLocalVariable> parameters;

  public CallStackFrame(StackFrame frame, JType classType, JMethod method, List<JLocalVariable> parameters, int lineNumber) {
    this.stackFrame = frame;
    this.classType = classType;
    this.method = method;
    this.parameters = parameters;
    this.lineNumber = lineNumber;
  }

  public StackFrame getStackFrame() {
    return stackFrame;
  }

  public JType getClassType() {
    return classType;
  }

  public JMethod getMethod() {
    return method;
  }

  public String getSimpleMethodHeader() {
    StringBuilder builder = new StringBuilder();

    builder.append(classType.getSimpleName());
    builder.append(".");
    builder.append(method.name());
    builder.append("(");
    for (int i = 0; i < parameters.size(); i++) {
      builder.append(parameters.get(i).getType().getSimpleName());
      builder.append(" ");
      builder.append(parameters.get(i).name());
      if (i < parameters.size() - 1) {
        builder.append(", ");
      }
    }
    builder.append(")");

    return builder.toString();
  }

  public String getFullMethodHeader() {
    StringBuilder builder = new StringBuilder();

    builder.append(classType.name());
    builder.append(".");
    builder.append(method.name());
    builder.append("(");
    for (int i = 0; i < parameters.size(); i++) {
      builder.append(parameters.get(i).getType().name());
      builder.append(" ");
      builder.append(parameters.get(i).name());
      if (i < parameters.size() - 1) {
        builder.append(", ");
      }
    }
    builder.append(")");

    return builder.toString();
  }

  public List<JLocalVariable> getParameters() {
    return parameters;
  }

  public int getLineNumber() {
    return lineNumber;
  }
}