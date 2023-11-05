package com.hagenberg.jarvis.graph.rendering.renderers;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.jarvis.graph.rendering.Link;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.flag.ImNodesColorStyle;

public class LocalVariableRenderer {
  public void render(LocalGVariable var, List<Link> links) {
    int nodeId = (int) var.getNodeId();
    ImNodes.pushColorStyle(ImNodesColorStyle.TitleBar, Colors.LocalVariable);
    ImNodes.beginNode(nodeId);

    ImNodes.setNodeDraggable(nodeId, false);
    ImNodes.setNodeGridSpacePos(nodeId, var.getPosition().x, var.getPosition().y);


    ImNodes.beginNodeTitleBar();
    ImGui.textColored(Colors.Type, var.getStaticType());
    ImGui.sameLine();
    ImGui.text(var.getName());
    ImNodes.endNodeTitleBar();

    ImNodes.popColorStyle();

    boolean isPrimitive = var.getNode() instanceof PrimitiveGNode;

    if (isPrimitive) {
      ImNodes.pushColorStyle(ImNodesColorStyle.Pin, Colors.Invisible);
      ImNodes.pushColorStyle(ImNodesColorStyle.PinHovered, Colors.Invisible);
    }

    ImNodes.beginOutputAttribute(nodeId);

    if (isPrimitive) {
      PrimitiveGNode prim = (PrimitiveGNode) var.getNode();

      ImGui.text(prim.getPrimitiveValue().toString());

      ImNodes.popColorStyle();
      ImNodes.popColorStyle();
    } else if (var.getNode() instanceof ObjectGNode obj) {
      ImGui.text("Reference to Object#" + obj.getObjectId());
      links.add(new Link(nodeId, obj.getNodeId()));
    }

    ImNodes.endOutputAttribute();

    ImNodes.endNode();
  }
}
