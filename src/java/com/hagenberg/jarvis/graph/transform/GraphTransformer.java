package com.hagenberg.jarvis.graph.transform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.hagenberg.jarvis.graph.render.RenderModel;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.util.Observer;
import com.hagenberg.jarvis.views.ObjectGraph;

import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;

public class GraphTransformer implements Observer {
  private final ObjectGraphModel ogm;
  private final ObjectGraph og;
  private RenderModel rm;

  private final TransformerRegistry registry = new TransformerRegistry(this::showTransformerContextMenu);

  private Thread transformerThread = null;
  private boolean transformPending = false;

  private final IdPool idPool = new IdPool(0);
  private final Stack<JObjectReference> objectsToTransform = new Stack<>();
  private final Set<PendingLink> pendingLinks = new HashSet<>();
  private Map<JObjectReference, Node> transformationMap = new HashMap<>();
  private Map<Integer, JObjectReference> reverseTransformationMap = new HashMap<>();

  public GraphTransformer(ObjectGraphModel ogm, ObjectGraph og) {
    this.ogm = ogm;
    this.og = og;
    ogm.addObserver(this);
  }

  /**
   * Triggers a graph transformation for OGM -> RM. This happens in parallel to
   * the rendering thread.
   */
  @Override
  public void update() {
    if (transformerThread != null && transformerThread.isAlive()) {
      transformPending = true;
      return;
    }

    transformerThread = new Thread(this::transformGraph);
    transformerThread.start();
  }

  /**
   * Transforms the ObjectGraphModel into a RenderModel. This happens in parallel
   * to the rendering thread via "double buffering". The whole transformation is
   * done in two passes: 1. Transformation pass: Transforms all objects and local
   * variables into renderable nodes 2. Connection pass: Resolves pending links
   * between nodes into actual links
   */
  private void transformGraph() {
    rm = new RenderModel(); // get new render model to populate
    transformationPass();
    connectionPass();
    og.stageRenderModel(rm); // stage render model for rendering

    synchronized (this) {
      if (transformPending) { // check if another update was triggered while transformation was running
        transformPending = false;
        update();
      }
    }
  }

  /**
   * Transforms all objects and local variables into renderable nodes
   */
  private void transformationPass() {
    // get previous transformation map for position migration
    Map<JObjectReference, Node> oldTransformationMap = transformationMap;

    // clear old data
    idPool.reset();
    transformationMap = new HashMap<>();
    reverseTransformationMap.clear();
    objectsToTransform.clear();

    for (LocalGVariable root : ogm.getLocalVariables()) {
      Node node = registry.getLocalVarTransformer(root).transform(root, idPool, (source, id, target) -> {
        pendingLinks.add(new PendingLink(source, id, target));
        if (!transformationMap.containsKey(target) && !objectsToTransform.contains(target)) {
          objectsToTransform.push(target);
        }
      });
      // no need for transformation map here, as local variables take no input
      // connections
      rm.addRoot(node);
    }

    while (!objectsToTransform.isEmpty()) {
      JObjectReference object = objectsToTransform.pop();
      Node node = registry.getObjectTransformer(object).transform(object, idPool, (source, id, target) -> {
        pendingLinks.add(new PendingLink(source, id, target));
        if (!transformationMap.containsKey(target) && !objectsToTransform.contains(target) && !target.equals(object)) {
          objectsToTransform.push(target);
        }
      });
      transformationMap.put(object, node);
      reverseTransformationMap.put(node.getNodeId(), object);

      // migrate position and freeze from previous transformation
      Node oldNode = oldTransformationMap.get(object);
      if (oldNode != null) {
        node.setPosition(oldNode.getPosition());
        node.setFrozen(oldNode.isFrozen());
      }

      transformationMap.put(object, node);
      rm.addChild(node);
    }
  }

  /**
   * Resolves pending links between nodes into actual links
   */
  private void connectionPass() {

    Map<Integer, Node> debug = new HashMap<>();

    for (PendingLink pendingLink : pendingLinks) {
      Node source = pendingLink.getSource();
      Node target = transformationMap.get(pendingLink.getTarget());

      source.getOutNeighbors().add(target);
      target.getInNeighbors().add(source);

      rm.addLink(pendingLink.getAttributeId(), transformationMap.get(pendingLink.getTarget()).getNodeId());
      debug.put(pendingLink.getAttributeId(), target);
    }

    pendingLinks.clear();
  }

  public TransformerRegistry getRegistry() {
    return registry;
  }

  public void showTransformerContextMenu(int nodeId) {
    JObjectReference originNode = reverseTransformationMap.get(nodeId);

    if (ImGui.isItemHovered() && ImGui.isMouseReleased(ImGuiMouseButton.Right)) {
      ImGui.openPopup("NodeCtx##" + nodeId);
    }

    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5); // NodeEditor somehow overrides this so we have to set it here

    if (ImGui.beginPopup("NodeCtx##" + nodeId)) {
      ImGui.menuItem("Settings", "", false, false);
      if (ImGui.beginMenu("Renderer for this Object")) {
        List<NodeTransformer<JObjectReference>> transformers = registry.getObjectTransformers();
        NodeTransformer<JObjectReference> currentTransformer = registry.getSpecificOT(originNode);
        for (NodeTransformer<JObjectReference> transformer : transformers) {
          boolean selected = transformer == currentTransformer;
          if (ImGui.menuItem(transformer.getName(), "", selected)) {
            registry.setObjectTransformer(originNode, transformer);
            if (!selected) {
              update();
            }
          }
        }
        if (ImGui.menuItem("[Default]", "", currentTransformer == null)) {
          registry.setObjectTransformer(originNode, null);
          if (currentTransformer != null) {
            update();
          }
        }
        ImGui.endMenu();
      }
      if (ImGui.beginMenu("Renderer for this Type")) {
        List<NodeTransformer<JObjectReference>> transformers = registry.getObjectTransformers();
        NodeTransformer<JObjectReference> currentTransformer = registry.getSpecificOTForType(originNode);
        for (NodeTransformer<JObjectReference> transformer : transformers) {
          boolean selected = transformer == currentTransformer;
          if (ImGui.menuItem(transformer.getName(), "", selected)) {
            registry.setObjectTransformer(originNode.type(), transformer);
            if (!selected) {
              update();
            }
          }
        }
        if (ImGui.menuItem("[Default]", "", currentTransformer == null)) {
          registry.setObjectTransformer(originNode.type(), null);
          if (currentTransformer != null) {
            update();
          }
        }
        ImGui.endMenu();
      }
      ImGui.endPopup();
    }
    ImGui.popStyleVar();
  }
}
