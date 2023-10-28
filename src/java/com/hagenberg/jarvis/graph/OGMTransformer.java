package com.hagenberg.jarvis.graph;

import java.util.HashSet;
import java.util.Set;

import com.hagenberg.jarvis.models.ObjectGraphModel;

/**
 * This class manages the transformation of the object graph model to a simple
 * graph model that is used for layouting and rendering.
 */
public class OGMTransformer {
  private Set<LayoutableNode> nodesToLayout = new HashSet<>();

  /** 
   * Transforms the set OGM to a simple graph model that is used for layouting.
   * This is done incrementally, i.e. only the nodes that have changed since the
   * last transformation are updated.
   */
  public void transform(ObjectGraphModel ogm) {
    for (LayoutableNode layoutableNode : ogm.getObjects()) {
      if (layoutableNode.isLayouted()) {
        nodesToLayout.add(layoutableNode);
      }
    }

    for (LayoutableNode layoutableNode : ogm.getLocalVars()) {
      if (layoutableNode.isLayouted()) {
        nodesToLayout.add(layoutableNode);
      }
    }
  }

  public Iterable<LayoutableNode> getNodes() {
    return nodesToLayout;
  }
}
