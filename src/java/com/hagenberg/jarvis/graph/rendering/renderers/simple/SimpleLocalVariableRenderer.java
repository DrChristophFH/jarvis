package com.hagenberg.jarvis.graph.rendering.renderers.simple;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.Vec2;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.graph.rendering.renderers.Renderer;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;
import com.hagenberg.jarvis.views.ObjectGraph;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.flag.ImNodesColorStyle;

public class SimpleLocalVariableRenderer extends Renderer<LocalGVariable> {

  public SimpleLocalVariableRenderer(String name, RendererRegistry registry) {
    super(LocalGVariable.class, name, registry);
  }

  @Override
  public void render(LocalGVariable var, int nodeId, ObjectGraph graph) {
    ImNodes.pushColorStyle(ImNodesColorStyle.TitleBar, Colors.LocalVariable);
    ImNodes.beginNode(nodeId);
    
    graph.registerRootForLayout(var.getLayoutNode());

    ImNodes.setNodeDraggable(nodeId, true);
    Vec2 position = var.getLayoutNode().getPosition();
    ImNodes.setNodeGridSpacePos(nodeId, position.x, position.y);
    var.getLayoutNode().setLength((int) ImNodes.getNodeDimensionsX(nodeId));

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
    Snippets.drawTypeWithTooltip(var.getStaticTypeName(), tooltip);
    ImGui.sameLine();
    ImGui.text(var.getName());

    if (isPrimitive) {
      PrimitiveGNode prim = (PrimitiveGNode) var.getNode();
      registry.getPrimitiveRenderer(var).render(prim, nodeId, graph);
      ImNodes.endStaticAttribute();
    } else if (var.getNode() instanceof ObjectGNode obj) {
      ImGui.sameLine();
      ImGui.text("Reference to Object#" + obj.getObjectId());
      graph.addLink(nodeId, obj);
      var.getLayoutNode().link(obj.getLayoutNode());
      ImNodes.endOutputAttribute();
    }

    ImNodes.endNode();
  }
}
