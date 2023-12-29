package com.hagenberg.jarvis.graph.transform;

import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;

public class PendingLink {
  private final int attributeId;
  private final Node source;
  private final JObjectReference target;

  public PendingLink(Node source, int attId, JObjectReference target) {
    this.attributeId = attId;
    this.target = target;
    this.source = source;
  }

  public int getAttributeId() {
    return attributeId;
  }

  public JObjectReference getTarget() {
    return target;
  }

  public Node getSource() {
    return source;
  }
}
