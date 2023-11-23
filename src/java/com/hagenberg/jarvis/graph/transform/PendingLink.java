package com.hagenberg.jarvis.graph.transform;

import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

public class PendingLink {
  private final int attributeId;
  private final Node source;
  private final ObjectGNode target;

  public PendingLink(Node source, int attId, ObjectGNode target) {
    this.attributeId = attId;
    this.target = target;
    this.source = source;
  }

  public int getAttributeId() {
    return attributeId;
  }

  public ObjectGNode getTarget() {
    return target;
  }

  public Node getSource() {
    return source;
  }
}
