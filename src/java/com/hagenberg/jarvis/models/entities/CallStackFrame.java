package com.hagenberg.jarvis.models.entities;

import java.util.List;

import com.hagenberg.jarvis.models.entities.wrappers.JLocalVariable;
import com.hagenberg.jarvis.util.TypeFormatter;
import com.sun.jdi.Method;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Type;

public class CallStackFrame {
  private StackFrame stackFrame;
  private Type classType;
  private Method method;
  private int lineNumber;
  private List<JLocalVariable> parameters;

  public CallStackFrame(StackFrame frame, Type classType, Method method, List<JLocalVariable> parameters, int lineNumber) {
    this.stackFrame = frame;
    this.classType = classType;
    this.method = method;
    this.parameters = parameters;
    this.lineNumber = lineNumber;
  }

  public StackFrame getStackFrame() {
    return stackFrame;
  }

  public Type getClassType() {
    return classType;
  }

  public Method getMethod() {
    return method;
  }

  public String getSimpleMethodHeader() {
    StringBuilder builder = new StringBuilder();

    builder.append(TypeFormatter.getSimpleType(classType.name()));
    builder.append(".");
    builder.append(method.name());
    builder.append("(");
    for (int i = 0; i < parameters.size(); i++) {
      builder.append(TypeFormatter.getSimpleType(parameters.get(i).getStaticTypeName()));
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
      builder.append(TypeFormatter.getSimpleType(parameters.get(i).getStaticTypeName()));
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