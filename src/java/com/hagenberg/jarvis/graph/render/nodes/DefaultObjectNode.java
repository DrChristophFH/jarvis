package com.hagenberg.jarvis.graph.render.nodes;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.components.Tooltip;
import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.transform.TransformerContextMenu;
import com.hagenberg.jarvis.models.entities.wrappers.JType;
import com.hagenberg.jarvis.models.entities.wrappers.ReferenceHolder;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.flag.ImNodesColorStyle;

public class DefaultObjectNode extends Node {

  protected final JType type;
  protected final String objectName;
  protected final String toString;
  protected final List<ReferenceHolder> referenceHolders;
  protected final Tooltip tooltip = new Tooltip();
  protected final TransformerContextMenu transformerContextMenu;

  private boolean frozenStyle = false;

  public DefaultObjectNode(int nodeId, JType type, String objectName, String toString, List<ReferenceHolder> referenceHolders,
      TransformerContextMenu transformerContextMenu) {
    super(nodeId);
    this.type = type;
    this.objectName = objectName;
    this.toString = toString;
    this.referenceHolders = referenceHolders;
    this.transformerContextMenu = transformerContextMenu;
  }

  @Override
  protected void preNode() {
    if (isFrozen()) {
      frozenStyle = true;
      ImNodes.pushColorStyle(ImNodesColorStyle.NodeOutline, Colors.NodeOutlineFrozen);
    }
    super.preNode();
  }

  @Override
  protected void headerContent() {
    Snippets.drawTypeWithTooltip(type, tooltip);
    ImGui.sameLine();
    ImGui.text(objectName);
  }

  @Override
  protected void content() {
    transformerContextMenu.show(nodeId);

    if (ImGui.beginPopup("NodeCtx##" + nodeId)) {
      if (ImGui.menuItem("Freeze", "", isFrozen())) {
        setFrozen(!isFrozen());
      }
      ImGui.endPopup();
    }

    showRefInputAttribute();

    ImGui.textColored(Colors.Type, "toString():");
    ImGui.sameLine();
    ImGui.pushTextWrapPos(ImGui.getCursorPosX() + Math.min(ImNodes.getNodeDimensionsX(nodeId), 500)); // TODO: make word wrap configurable
    ImGui.text(toString);
    ImGui.popTextWrapPos();

    for (Attribute attribute : attributes) {
      attribute.render();
    }
  }

  @Override
  protected void postNode() {
    if (frozenStyle) {
      ImNodes.popColorStyle();
      frozenStyle = false;
    }
    super.postNode();
  }

  protected void showRefInputAttribute() {
    // Reference attribute has node id
    ImNodes.beginInputAttribute(nodeId);
    ImGui.text(referenceHolders.size() + " references");
    tooltip.show(() -> {
      for (int i = 0; i < referenceHolders.size(); i++) {
        ImGui.text("-> " + referenceHolders.get(i).name());
      }
    });
    ImNodes.endInputAttribute();
  }
}
