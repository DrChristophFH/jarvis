package com.hagenberg.jarvis.models.entities.graph;

import com.sun.jdi.Type;

public abstract class GNode {
  protected Type type;

  public GNode(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public String getTypeName () {
    return type.name();
  }
}
