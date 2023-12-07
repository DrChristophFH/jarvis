package com.hagenberg.jarvis.models.entities.graph;

import com.sun.jdi.LocalVariable;
import com.sun.jdi.Type;

public class LocalGVariable extends GVariable {
  private LocalVariable localVariable; // The local variable this graph variable represents

  public LocalGVariable(String name, Type staticType, LocalVariable localVariable) {
    super(name, staticType);
    this.localVariable = localVariable;
  }

  public LocalVariable getLocalVariable() {
    return localVariable;
  }
}