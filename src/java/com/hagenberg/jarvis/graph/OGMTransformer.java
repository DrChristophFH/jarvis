package com.hagenberg.jarvis.graph;

import java.util.HashSet;
import java.util.Set;

import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.util.Observer;

public class OGMTransformer implements Observer {
  private Set<LayoutableNode> nodesToLayout = new HashSet<>();
  private ObjectGraphModel ogm;

  public OGMTransformer(ObjectGraphModel ogm) {
    this.ogm = ogm;
    ogm.addObserver(this);
  }

  @Override
  public void update() {
    transform();
  }

  /**
   * Transforms the given ObjectGraphModel by adding all layouted nodes from the
   * objects and local variables to the nodesToLayout list.
   *
   * @param ogm the ObjectGraphModel to transform
   */
  public void transform() {
    nodesToLayout.clear();

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
