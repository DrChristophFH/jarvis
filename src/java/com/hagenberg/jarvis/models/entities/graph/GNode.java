package com.hagenberg.jarvis.models.entities.graph;

public abstract class GNode {
  protected String type;

  public GNode(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
