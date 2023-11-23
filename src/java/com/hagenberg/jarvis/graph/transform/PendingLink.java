package com.hagenberg.jarvis.graph.transform;

import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

public class PendingLink {
  private final int transformedAttId;
  private final ObjectGNode target;

  public PendingLink(int transformedAttId, ObjectGNode target) {
    this.transformedAttId = transformedAttId;
    this.target = target;
  }

  public int getTransformedAttId() {
    return transformedAttId;
  }

  public ObjectGNode getTarget() {
    return target;
  }
}
