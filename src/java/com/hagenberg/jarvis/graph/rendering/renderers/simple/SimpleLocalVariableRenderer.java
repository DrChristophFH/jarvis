package com.hagenberg.jarvis.graph.rendering.renderers.simple;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.jarvis.graph.rendering.Link;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.graph.rendering.renderers.Renderer;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;
import com.hagenberg.jarvis.util.Snippets;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.flag.ImNodesColorStyle;

public class SimpleLocalVariableRenderer extends Renderer<LocalGVariable> {

  public SimpleLocalVariableRenderer(String name, RendererRegistry registry) {
    super(LocalGVariable.class, name, registry);
  }

  @Override
  public void render(LocalGVariable var, int nodeId, List<Link> links) {
    ImNodes.pushColorStyle(ImNodesColorStyle.TitleBar, Colors.LocalVariable);
    ImNodes.beginNode(nodeId);

    ImNodes.setNodeDraggable(nodeId, true);
    ImNodes.setNodeGridSpacePos(nodeId, var.getPosition().x, var.getPosition().y);


    ImNodes.beginNodeTitleBar();
    ImGui.text("Local Variable: " + var.getName());
    ImNodes.endNodeTitleBar();

    ImNodes.popColorStyle();

    boolean isPrimitive = var.getNode() instanceof PrimitiveGNode;

    if (isPrimitive) {
      ImNodes.beginStaticAttribute(nodeId);
    } else {
      ImNodes.beginOutputAttribute(nodeId);
    }

    Snippets.drawTypeWithTooltip(var.getStaticType());
    ImGui.sameLine();
    ImGui.text(var.getName());

    if (isPrimitive) {
      PrimitiveGNode prim = (PrimitiveGNode) var.getNode();
      registry.getPrimitiveRenderer(var).render(prim, nodeId, links);
      ImNodes.endStaticAttribute();
    } else if (var.getNode() instanceof ObjectGNode obj) {
      ImGui.sameLine();
      ImGui.text("Reference to Object#" + obj.getObjectId());
      links.add(new Link(nodeId, obj.getNodeId()));
      ImNodes.endOutputAttribute();
    }

    ImNodes.endNode();
  }
}
