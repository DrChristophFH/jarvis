package com.hagenberg.jarvis.graph.rendering.renderers.simple;

import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.graph.rendering.renderers.Renderer;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;
import com.hagenberg.jarvis.views.ObjectGraph;

import imgui.ImGui;

public class SimplePrimitiveRenderer extends Renderer<PrimitiveGNode> {

  public SimplePrimitiveRenderer(String name, RendererRegistry registry) {
    super(PrimitiveGNode.class, name, registry);
  }

  @Override
  public void render(PrimitiveGNode node, int id, ObjectGraph graph) {
    ImGui.sameLine();
    ImGui.text(node.getPrimitiveValue().toString());
  }
}
