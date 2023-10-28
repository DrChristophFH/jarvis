package com.hagenberg.jarvis.graph;

public class LayoutableEdgeImpl implements LayoutableEdge {
  private LayoutableNode source;
  private LayoutableNode target;

  public LayoutableEdgeImpl(LayoutableNode source, LayoutableNode target) {
    this.source = source;
    this.target = target;
  }

  @Override
  public LayoutableNode getSource() {
    return source;
  }

  @Override
  public LayoutableNode getTarget() {
    return target;
  }
  
}
