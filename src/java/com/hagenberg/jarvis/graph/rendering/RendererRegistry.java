package com.hagenberg.jarvis.graph.rendering;

import java.util.HashMap;
import java.util.Map;

import com.hagenberg.jarvis.graph.LayoutableNode;
import com.hagenberg.jarvis.graph.rendering.renderers.NodeRenderer;

public class RendererRegistry {

  private static RendererRegistry instance = new RendererRegistry();

  private Map<LayoutableNode, NodeRenderer> customNodeRenderers = new HashMap<>();
  private Map<Class<?>, NodeRenderer> defaultRenderers = new HashMap<>();

  private RendererRegistry() {
  }

  public static RendererRegistry getInstance() {
    return instance;
  }

  public NodeRenderer getRenderer(LayoutableNode node) {
    NodeRenderer renderer = customNodeRenderers.get(node);
    if (renderer != null) return renderer;
    return defaultRenderers.get(node.getClass());
  }

  public void setRendererForNode(LayoutableNode node, NodeRenderer renderer) {
    customNodeRenderers.put(node, renderer);
  }

  public void setRendererForClass(Class<?> nodeClass, NodeRenderer renderer) {
    defaultRenderers.put(nodeClass, renderer);
  }
}
