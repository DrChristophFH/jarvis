package com.hagenberg.jarvis.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.graph.ArrayGNode;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
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
   * Transforms the given ObjectGraphModel by adding all layouted nodes from the objects and local variables to the nodesToLayout
   * list.
   *
   * @param ogm the ObjectGraphModel to transform
   */
  public void transform() {
    nodesToLayout.clear();

    int nodeId = 0;

    for (LayoutableNode layoutableNode : ogm.getObjects()) {
      if (layoutableNode.isLayouted()) {
        nodesToLayout.add(layoutableNode);
        layoutableNode.setNodeId(nodeId++);
        // make space for member attribute ids
        if (layoutableNode instanceof ArrayGNode arr) {
          nodeId += arr.getContentGVariables().size();
        } else if (layoutableNode instanceof ObjectGNode obj) {
          nodeId += obj.getMembers().size();
        }
      }
    }

    for (LayoutableNode layoutableNode : ogm.getLocalVariables()) {
      if (layoutableNode.isLayouted()) {
        nodesToLayout.add(layoutableNode);
        layoutableNode.setNodeId(nodeId++);
      }
    }
  }

  public Set<LayoutableNode> getNodes() {
    return nodesToLayout;
  }
}
