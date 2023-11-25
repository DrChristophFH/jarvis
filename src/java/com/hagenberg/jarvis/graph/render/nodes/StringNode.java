package com.hagenberg.jarvis.graph.render.nodes;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.jarvis.graph.transform.TransformerRegistry;
import com.hagenberg.jarvis.models.entities.graph.GVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.util.Procedure;

import imgui.ImGui;

public class StringNode extends DefaultObjectNode {

  public StringNode(int nodeId, String typeName, String objectName, String toString, List<GVariable> referenceHolders,
      TransformerRegistry registry, ObjectGNode originNode, Procedure triggerRetransform) {
    super(nodeId, typeName, objectName, toString, referenceHolders, registry, originNode, triggerRetransform);
  }

  @Override
  protected void headerContent() {
    super.headerContent();
    ImGui.textColored(Colors.Attention, "[String simplified]");
  }

  @Override
  protected void content() {
    transformerContextMenu(registry, originNode, triggerRetransform);

    showRefInputAttribute();
    ImGui.textColored(Colors.Identifier, "value: ");
    ImGui.sameLine();
    ImGui.text(toString);
  }
}
