package com.hagenberg.jarvis.graph.rendering.renderers.simple;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.graph.rendering.renderers.Renderer;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;
import com.hagenberg.jarvis.views.ObjectGraph;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;

public class SimpleFieldRenderer extends Renderer<MemberGVariable> {

  public SimpleFieldRenderer(String name, RendererRegistry registry) {
    super(MemberGVariable.class, name, registry);
  }

  @Override
  public void render(MemberGVariable var, int attId, ObjectGraph graph) {
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
    ImGui.textColored(Colors.Identifier, var.getName());
    
    if (isPrimitive) {
      PrimitiveGNode prim = (PrimitiveGNode) var.getNode();
      registry.getPrimitiveRenderer(var).render(prim, attId, graph);
      ImNodes.endStaticAttribute();
    } else if (var.getNode() instanceof ObjectGNode obj) {
      ImGui.sameLine();
      ImGui.text("Reference to Object#" + obj.getObjectId());
      graph.addLink(attId, obj);
      var.getContainingObject().getLayoutNode().link(obj.getLayoutNode());
      ImNodes.endOutputAttribute();
    } else {
      ImGui.sameLine();
      ImGui.text("null");
      ImNodes.endOutputAttribute();
    }
  }
}
