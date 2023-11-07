package com.hagenberg.jarvis.graph;

import java.util.HashSet;
import java.util.Set;

import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.graph.ArrayGNode;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.util.Observer;

import imgui.extension.imnodes.ImNodes;

public class OGMTransformer implements Observer {
  private Set<LayoutableNode> nodesToLayout = new HashSet<>();
  private Set<LayoutableNode> rootsToLayout = new HashSet<>();
  private ObjectGraphModel ogm;
  private boolean recalcWidths = false;

  public OGMTransformer(ObjectGraphModel ogm) {
    this.ogm = ogm;
    ogm.addObserver(this);
  }

  @Override
  public void update() {
    transform();
    recalcWidths = true;
  }

  /**
   * Recalculates the widths of the nodes if the recalcWidths flag is set.
   * This method can only run AFTER the nodes have been drawn at least once!
   */
  public void recalcWidthsIfNecessary() {
    if (recalcWidths) {
      System.out.println("Recalculating widths");
      recalcWidths = false;
      for (LayoutableNode node : nodesToLayout) {
        node.setLength((int)ImNodes.getNodeDimensionsX(node.getNodeId()));
      }

      for (LayoutableNode node : rootsToLayout) {
        node.setLength((int)ImNodes.getNodeDimensionsX(node.getNodeId()));
      }
    }
  }

  /**
   * Transforms the given ObjectGraphModel by adding all layouted nodes from the objects and local variables to the nodesToLayout
   * list.
   *
   * @param ogm the ObjectGraphModel to transform
   */
  public void transform() {
    nodesToLayout.clear();
    rootsToLayout.clear();

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
        rootsToLayout.add(layoutableNode);
        layoutableNode.setNodeId(nodeId++);
      }
    }
  }

  public Set<LayoutableNode> getNodes() {
    return nodesToLayout;
  }

  public Set<LayoutableNode> getRoots() {
    return rootsToLayout;
  }
}
