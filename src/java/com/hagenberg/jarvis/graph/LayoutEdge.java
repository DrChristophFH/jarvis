package com.hagenberg.jarvis.graph;

public class LayoutEdge {
  public LayoutNode source;
  public LayoutNode target;

  public LayoutEdge(LayoutNode source, LayoutNode target) {
    this.source = source;
    this.target = target;
  }
}