package com.hagenberg.jarvis.graph.render.nodes;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.jarvis.graph.transform.TransformerContextMenu;
import com.hagenberg.jarvis.models.entities.wrappers.JType;
import com.hagenberg.jarvis.models.entities.wrappers.ReferenceHolder;

import imgui.ImGui;

public class StringNode extends DefaultObjectNode {

  public StringNode(int nodeId, JType type, String objectName, String toString, List<ReferenceHolder> referenceHolders,
      TransformerContextMenu transformerContextMenu) {
    super(nodeId, type, objectName, toString, referenceHolders, transformerContextMenu);
  }

  @Override
  protected void headerContent() {
    super.headerContent();
    ImGui.sameLine();
    ImGui.textColored(Colors.Attention, "[String simplified]");
  }

  @Override
  protected void content() {
    transformerContextMenu.show(nodeId);

    showRefInputAttribute();
    ImGui.textColored(Colors.Identifier, "value: ");
    ImGui.sameLine();
    ImGui.text(toString);
  }
}
