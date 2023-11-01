package com.hagenberg.jarvis.models.entities.graph;

public class ContentGVariable extends GVariable {
  private final int index;
  private final ObjectGNode containingObject;

  public ContentGVariable(String name, String staticType, ObjectGNode parentObj, int index) {
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
