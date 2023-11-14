package com.hagenberg.jarvis.graph.rendering.renderers.simple;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.jarvis.graph.rendering.Link;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.graph.rendering.renderers.Renderer;
import com.hagenberg.jarvis.models.entities.graph.ArrayGNode;
import com.hagenberg.jarvis.models.entities.graph.ContentGVariable;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.util.TypeFormatter;

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
    Snippets.drawTypeWithTooltip(node.getTypeName(), tooltip);
    ImGui.sameLine();
    ImGui.text("Object#" + node.getObjectId());
    ImNodes.endNodeTitleBar();

    int attId = nodeId;

    // Reference attribute has node id
    ImNodes.beginInputAttribute(attId++);
    ImGui.text(node.getReferenceHolders().size() + " references");
    tooltip.show(() -> {
      for (int i = 0; i < node.getReferenceHolders().size(); i++) {
        ImGui.text("-> " + node.getReferenceHolders().get(i).getName());
      }
    });
    ImNodes.endInputAttribute();

    ImGui.textColored(Colors.Type, "toString():");
    ImGui.sameLine();
    ImGui.pushTextWrapPos(ImGui.getCursorPosX() + ImNodes.getNodeDimensionsX(nodeId)); // TODO: make word wrap configurable
    ImGui.text(node.getToString());
    ImGui.popTextWrapPos();

    for (MemberGVariable member : node.getMembers()) {
      registry.getMemberRenderer(member).render(member, attId, links);

      Snippets.memberContextMenu(attId, member, registry);

      attId++;
    }

    if (node instanceof ArrayGNode array) {
      ImGui.textColored(Colors.Type, "Array:");
      ImGui.sameLine();
      ImGui.text("length: " + array.getContentGVariables().size());
      for (ContentGVariable content : array.getContentGVariables()) {
        if (content.getNode() == null) {
          continue;
        }
        registry.getContentRenderer(content).render(content, attId, links);
        attId++;
      }
    }

    ImNodes.endNode();
  }
}
