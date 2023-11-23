package com.hagenberg.jarvis.models.entities.graph;

import com.sun.jdi.LocalVariable;
import com.sun.jdi.Type;

public class LocalGVariable extends GVariable {
  private final StackFrameInformation stackFrameInformation; // A class to store stack frame information
  private LocalVariable localVariable; // The local variable this graph variable represents

  public LocalGVariable(String name, Type staticType, LocalVariable localVariable, StackFrameInformation stackFrameInformation) {
    super(name, staticType);
    this.localVariable = localVariable;
    this.stackFrameInformation = stackFrameInformation;
  }

  public StackFrameInformation getStackFrameInformation() {
    return stackFrameInformation;
  }

  public LocalVariable getLocalVariable() {
    return localVariable;
  }
}