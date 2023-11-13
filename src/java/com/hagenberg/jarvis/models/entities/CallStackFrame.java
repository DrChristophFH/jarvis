package com.hagenberg.jarvis.models.entities;

import java.util.List;

import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.sun.jdi.Method;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Type;

public class CallStackFrame {
  private StackFrame stackFrame;
  private Type classType;
  private Method method;
  private int lineNumber;
  private List<LocalGVariable> parameters;

  public CallStackFrame(StackFrame frame, Type classType, Method method, List<LocalGVariable> parameters, int lineNumber) {
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

  public String getFullMethodHeader() {
    StringBuilder builder = new StringBuilder();

    builder.append(classType.name());
    builder.append(".");
    builder.append(method.name());
    builder.append("(");
    for (int i = 0; i < parameters.size(); i++) {
      builder.append(parameters.get(i).getStaticTypeName());
      builder.append(" ");
      builder.append(parameters.get(i).getName());
      if (i < parameters.size() - 1) {
        builder.append(", ");
      }
    }
    builder.append(")");

    return builder.toString();
  }

  public List<LocalGVariable> getParameters() {
    return parameters;
  }

  public int getLineNumber() {
    return lineNumber;
  }
}