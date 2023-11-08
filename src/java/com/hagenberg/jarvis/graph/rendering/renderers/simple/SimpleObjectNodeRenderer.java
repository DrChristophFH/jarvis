package com.hagenberg.jarvis.graph.rendering.renderers.simple;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.jarvis.graph.rendering.Link;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.graph.rendering.renderers.Renderer;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.util.Snippets;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;

public class SimpleObjectNodeRenderer extends Renderer<ObjectGNode> {
  
  public SimpleObjectNodeRenderer(String name, RendererRegistry registry) {
    super(ObjectGNode.class, name, registry);
  }

  @Override
  public void render(ObjectGNode node, int nodeId, List<Link> links) {
    ImNodes.beginNode(nodeId);

    ImNodes.setNodeDraggable(nodeId, true);
    ImNodes.setNodeGridSpacePos(nodeId, node.getPosition().x, node.getPosition().y);

    ImNodes.beginNodeTitleBar();
    Snippets.drawTypeWithTooltip(node.getType());
    ImGui.sameLine();
    ImGui.text("Object#" + node.getObjectId());
    ImNodes.endNodeTitleBar();

    int attId = nodeId;
    
    // Reference attribute has node id
    ImNodes.beginInputAttribute(attId++);
    ImGui.text(node.getReferenceHolders().size() + " references");
    ImNodes.endInputAttribute();
    
    ImGui.textColored(Colors.Type, "toString():");
    ImGui.sameLine();
    ImGui.pushTextWrapPos(ImGui.getCursorPosX() + ImNodes.getNodeDimensionsX(nodeId)); // TODO: make word wrap configurable
    ImGui.text(node.getToString());
    ImGui.popTextWrapPos();

    for (MemberGVariable member : node.getMembers()) {
      registry.getMemberRenderer(member).render(member, attId++, links);
      if (ImGui.button("to Binary##" + attId)) {
        System.out.println("Binary");
        registry.setRenderer(member.getField());
      }
    }

    ImNodes.endNode();
  }
}
