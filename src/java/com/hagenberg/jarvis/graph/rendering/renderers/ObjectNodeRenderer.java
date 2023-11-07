package com.hagenberg.jarvis.graph.rendering.renderers;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.jarvis.graph.rendering.Link;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;

public class ObjectNodeRenderer {
  public void render(ObjectGNode node, List<Link> links) {
    int nodeId = node.getNodeId();
    ImNodes.beginNode(nodeId);

    ImNodes.setNodeDraggable(nodeId, true);
    ImNodes.setNodeGridSpacePos(nodeId, node.getPosition().x, node.getPosition().y);

    ImNodes.beginNodeTitleBar();
    ImGui.textColored(Colors.Type, node.getType());
    ImGui.sameLine();
    ImGui.text("Object#" + node.getObjectId());
    ImNodes.endNodeTitleBar();

    int attId = nodeId;

    // Reference attribute has node id
    ImNodes.beginInputAttribute(attId++);
    ImGui.text(node.getReferenceHolders().size() + " references");
    ImNodes.endInputAttribute();

    for (MemberGVariable member : node.getMembers()) {
      RendererRegistry.getInstance().getMemberVariableRenderer(member).render(member, attId++, links);
    }

    ImNodes.endNode();
  }
}
