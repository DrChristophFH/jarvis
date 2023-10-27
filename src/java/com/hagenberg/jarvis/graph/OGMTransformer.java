package com.hagenberg.jarvis.graph;

import java.util.HashSet;
import java.util.Set;

import com.hagenberg.jarvis.models.ObjectGraphModel;

/**
 * This class manages the transformation of the object graph model to a simple
 * graph model that is used for layouting and rendering.
 */
public class OGMTransformer {
  private ObjectGraphModel ogm;
  private LayoutGraph layoutGraph = new LayoutGraph();

  // Constructor, takes in OGM
  public OGMTransformer(ObjectGraphModel ogm) {
    this.ogm = ogm;
  }

  public LayoutGraph getLayoutGraph() {
    return layoutGraph;
  }

  /**
   * Transforms the set OGM to a simple graph model that is used for layouting.
   * This is done incrementally, i.e. only the nodes that have changed since the
   * last transformation are updated.
   */
  public void transform() {

  }
}
