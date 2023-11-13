package com.hagenberg.jarvis.graph.rendering.renderers.simple;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.jarvis.graph.rendering.Link;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.graph.rendering.renderers.Renderer;
import com.hagenberg.jarvis.models.entities.graph.ContentGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;
import com.hagenberg.jarvis.util.Snippets;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;

public class SimpleContentRenderer extends Renderer<ContentGVariable> {

  public SimpleContentRenderer(String name, RendererRegistry registry) {
    super(ContentGVariable.class, name, registry);
  }

  @Override
  public void render(ContentGVariable var, int attId, List<Link> links) {
    boolean isPrimitive = var.getNode() instanceof PrimitiveGNode;
    
    if (isPrimitive) {
      ImNodes.beginStaticAttribute(attId);
    } else {
      ImNodes.beginOutputAttribute(attId);
    }

    ImGui.text(var.getName());
    
    if (isPrimitive) {
      PrimitiveGNode prim = (PrimitiveGNode) var.getNode();
      registry.getPrimitiveRenderer(var).render(prim, attId, links);
      ImNodes.endStaticAttribute();
    } else if (var.getNode() instanceof ObjectGNode obj) {
      ImGui.sameLine();
      ImGui.text("Reference to Object#" + obj.getObjectId());
      links.add(new Link(attId, obj.getNodeId()));
      ImNodes.endOutputAttribute();
    } else {
      ImGui.sameLine();
      ImGui.text("null");
      ImNodes.endOutputAttribute();
    }
  }
}
