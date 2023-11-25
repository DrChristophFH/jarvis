package com.hagenberg.jarvis.graph.render.nodes;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.components.Tooltip;
import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.transform.TransformerContextMenu;
import com.hagenberg.jarvis.models.entities.graph.GVariable;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;

public class DefaultObjectNode extends Node {

  protected final String typeName;
  protected final String objectName;
  protected final String toString;
  protected final List<GVariable> referenceHolders;
  protected final Tooltip tooltip = new Tooltip();
  protected final TransformerContextMenu transformerContextMenu;

  public DefaultObjectNode(int nodeId, String typeName, String objectName, String toString, List<GVariable> referenceHolders,
      TransformerContextMenu transformerContextMenu) {
    super(nodeId);
    this.typeName = typeName;
    this.objectName = objectName;
    this.toString = toString;
    this.referenceHolders = referenceHolders;
    this.transformerContextMenu = transformerContextMenu;
  }

  @Override
  protected void headerContent() {
    Snippets.drawTypeWithTooltip(typeName, tooltip);
    ImGui.sameLine();
    ImGui.text(objectName);
  }

  @Override
  protected void content() {
    transformerContextMenu.show(nodeId);

    showRefInputAttribute();

    ImGui.textColored(Colors.Type, "toString():");
    ImGui.sameLine();
    ImGui.pushTextWrapPos(ImGui.getCursorPosX() + ImNodes.getNodeDimensionsX(nodeId)); // TODO: make word wrap configurable
    ImGui.text(toString);
    ImGui.popTextWrapPos();

    for (Attribute attribute : attributes) {
      attribute.render();
    }
  }

  protected void showRefInputAttribute() {
    // Reference attribute has node id
    ImNodes.beginInputAttribute(nodeId);
    ImGui.text(referenceHolders.size() + " references");
    tooltip.show(() -> {
      for (int i = 0; i < referenceHolders.size(); i++) {
        ImGui.text("-> " + referenceHolders.get(i).getName());
      }
    });
    ImNodes.endInputAttribute();
  }
}
