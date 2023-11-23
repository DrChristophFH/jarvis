package com.hagenberg.jarvis.graph.transform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.hagenberg.jarvis.graph.render.RenderModel;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.util.Observer;

public class GraphTransformer implements Observer {
  private final ObjectGraphModel ogm;
  private final RenderModel rm;

  private final TransformerRegistry registry = new TransformerRegistry();
  
  private final IdPool idPool = new IdPool(0);
  private final Map<ObjectGNode, Node> transformationMap = new HashMap<>();
  private final Stack<ObjectGNode> objectsToTransform = new Stack<>();
  private final Set<PendingLink> pendingLinks = new HashSet<>();

  public GraphTransformer(ObjectGraphModel ogm, RenderModel rm) {
    this.ogm = ogm;
    this.rm = rm;
    ogm.addObserver(this);
  }

  @Override
  public void update() {
    transformGraph();
  }

  /**
   * Transforms the object graph into a graph of renderable nodes.
   */
  public void transformGraph() {
    idPool.reset();
    transformationMap.clear();
    objectsToTransform.clear();

    transformationPass();
    connectionPass();
  }

  private void transformationPass() {
    for (LocalGVariable root : ogm.getLocalVariables()) {
      Node node = registry.getLocalVarTransformer(root).transform(root, idPool, (id, target) -> {
        pendingLinks.add(new PendingLink(id, target));
      });
      // no need for transformation map here, as local variables take no input connections
      rm.addNode(node);
    }

    while (!objectsToTransform.isEmpty()) {
      ObjectGNode object = objectsToTransform.pop();
      Node node = registry.getObjectTransformer(object).transform(object, idPool, (id, target) -> {
        pendingLinks.add(new PendingLink(id, target));
      });
      transformationMap.put(object, node);
      rm.addNode(node);
    }
  }

  private void connectionPass() {
    for (PendingLink pendingLink : pendingLinks) {
      rm.addLink(pendingLink.getTransformedAttId(), transformationMap.get(pendingLink.getTarget()).getNodeId());
    }
  }
}
