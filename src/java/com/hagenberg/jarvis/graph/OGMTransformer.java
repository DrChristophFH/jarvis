package com.hagenberg.jarvis.graph;

import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.graph.ArrayGNode;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.util.Observer;

public class OGMTransformer implements Observer {
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
   * Assigns all nodes a unique id, reserving enough space for member attributes.
   */
  public void transform() {
    int nodeId = 0;

    for (ObjectGNode object : ogm.getObjects()) {
      object.getLayoutNode().setNodeId(nodeId++);
      // make space for member attribute ids
      if (object instanceof ArrayGNode arr) {
        nodeId += arr.getContentGVariables().size();
      } else if (object instanceof ObjectGNode obj) {
        nodeId += obj.getMembers().size();
      }
    }

    for (LocalGVariable localGVariable : ogm.getLocalVariables()) {
      localGVariable.getLayoutNode().setNodeId(nodeId++);
    }
  }
}
