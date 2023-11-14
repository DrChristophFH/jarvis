package com.hagenberg.jarvis.graph.rendering.renderers.simple;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.jarvis.graph.rendering.Link;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.graph.rendering.renderers.Renderer;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;

public class SimpleFieldRenderer extends Renderer<MemberGVariable> {

  public SimpleFieldRenderer(String name, RendererRegistry registry) {
    super(MemberGVariable.class, name, registry);
  }

  @Override
  public void render(MemberGVariable var, int attId, List<Link> links) {
    boolean isPrimitive = var.getNode() instanceof PrimitiveGNode;
    
    if (isPrimitive) {
      ImNodes.beginStaticAttribute(attId);
    } else {
      ImNodes.beginOutputAttribute(attId);
    }

    ImGui.textColored(Colors.AccessModifier, var.getAccessModifier().toString());
    ImGui.sameLine();
    Snippets.drawTypeWithTooltip(var.getStaticTypeName(), tooltip);
    ImGui.sameLine();
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
