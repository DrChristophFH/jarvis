package com.hagenberg.jarvis.graph.rendering;

import java.util.HashMap;
import java.util.Map;

import com.hagenberg.jarvis.graph.LayoutableNode;
import com.hagenberg.jarvis.graph.rendering.renderers.ObjectNodeRenderer;
import com.hagenberg.jarvis.graph.rendering.renderers.VariableRenderer;
import com.hagenberg.jarvis.models.entities.graph.GVariable;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.sun.jdi.*;

public class RendererRegistry {

  private static RendererRegistry instance = new RendererRegistry();

  // mapped to local variables
  private Map<LocalVariable, VariableRenderer> customVarRenderers = new HashMap<>();
  // mapped to object ids
  
  private VariableRenderer defaultVarRenderer = new VariableRenderer();
  private ObjectNodeRenderer defaultObjRenderer = new ObjectNodeRenderer();

  private RendererRegistry() {
  }

  public static RendererRegistry getInstance() {
    return instance;
  }

  public VariableRenderer getVariableRenderer(GVariable lvar) {
    return defaultVarRenderer;
  }

  public ObjectNodeRenderer getObjectRenderer(ObjectGNode obj) {
    return defaultObjRenderer;
  }

  // public NodeRenderer getNodeRenderer(LayoutableNode node) {
  //   NodeRenderer renderer = customNodeRenderers.get(node);
  //   if (renderer != null) return renderer;
  //   return defaultRenderers.get(node.getClass());
  // }

  // public void setRendererForNode(LayoutableNode node, NodeRenderer renderer) {
  //   customNodeRenderers.put(node, renderer);
  // }

  // public void setRendererForClass(Class<?> nodeClass, NodeRenderer renderer) {
  //   defaultRenderers.put(nodeClass, renderer);
  // }
}
