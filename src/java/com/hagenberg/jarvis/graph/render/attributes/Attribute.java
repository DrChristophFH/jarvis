package com.hagenberg.jarvis.graph.render.attributes;

import com.hagenberg.jarvis.graph.render.nodes.Node;

public abstract class Attribute {
  protected final int id;
  protected final Node parent;
  
  public Attribute(int id, Node parent) {
    this.id = id;
    this.parent = parent;
  }

  public int getId() {
    return id;
  }

  public Node getParent() {
    return parent;
  }

  public abstract void render();
}
