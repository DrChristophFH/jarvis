package com.hagenberg.jarvis.graph.render.nodes;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.components.Tooltip;
import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.transform.TransformerRegistry;
import com.hagenberg.jarvis.models.entities.graph.GVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.util.Procedure;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;

public class DefaultObjectNode extends Node {

  private final String typeName;
  private final String objectName;
  private final String toString;
  private final List<GVariable> referenceHolders;
  private final Tooltip tooltip = new Tooltip();
  private final TransformerRegistry registry;
  private final ObjectGNode originNode;
  private final Procedure triggerRetransform;

  public DefaultObjectNode(int nodeId, String typeName, String objectName, String toString, List<GVariable> referenceHolders,
      TransformerRegistry registry, ObjectGNode originNode, Procedure triggerRetransform) {
    super(nodeId);
    this.typeName = typeName;
    this.objectName = objectName;
    this.toString = toString;
    this.referenceHolders = referenceHolders;
    this.registry = registry;
    this.originNode = originNode;
    this.triggerRetransform = triggerRetransform;
  }

  @Override
  public void render() {
    ImNodes.beginNode(nodeId);

    ImNodes.setNodeDraggable(nodeId, true);
    ImNodes.setNodeGridSpacePos(nodeId, position.x, position.y);
    length = (int) ImNodes.getNodeDimensionsX(nodeId);

    ImNodes.beginNodeTitleBar();
    Snippets.drawTypeWithTooltip(typeName, tooltip);
    ImGui.sameLine();
    ImGui.text(objectName);
    ImNodes.endNodeTitleBar();

    transformerContextMenu(registry, originNode, triggerRetransform);

    // Reference attribute has node id
    ImNodes.beginInputAttribute(nodeId);
    ImGui.text(referenceHolders.size() + " references");
    tooltip.show(() -> {
      for (int i = 0; i < referenceHolders.size(); i++) {
        ImGui.text("-> " + referenceHolders.get(i).getName());
      }
    });
    ImNodes.endInputAttribute();

    ImGui.textColored(Colors.Type, "toString():");
    ImGui.sameLine();
    ImGui.pushTextWrapPos(ImGui.getCursorPosX() + ImNodes.getNodeDimensionsX(nodeId)); // TODO: make word wrap configurable
    ImGui.text(toString);
    ImGui.popTextWrapPos();

    for (Attribute attribute : attributes) {
      attribute.render();
    }

    ImNodes.endNode();
  }
}
