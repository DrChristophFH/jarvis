package com.hagenberg.jarvis.models.entities.graph;

import com.sun.jdi.Type;

public class ContentGVariable extends GVariable {
  private final int index;
  private final ObjectGNode containingObject;

  public ContentGVariable(String name, Type staticType, ObjectGNode parentObj, int index) {
    super(name, staticType);
    this.containingObject = parentObj;
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  public ObjectGNode getContainingObject() {
    return containingObject;
  }
}
