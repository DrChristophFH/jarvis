package com.hagenberg.jarvis.graph.render.attributes;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.components.Tooltip;
import com.hagenberg.jarvis.graph.render.nodes.Node;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;

public class DefaultLocalVariableAttribute extends Attribute {

  private final boolean isPrimitive;
  private final String typeName;
  private final String identifier;
  private final String value;
  private final Tooltip tooltip = new Tooltip();

  public DefaultLocalVariableAttribute(int id, Node parent, boolean isPrimitive, String typeName, String identifier, String value) {
    super(id, parent);
    this.isPrimitive = isPrimitive;
    this.typeName = typeName;
    this.identifier = identifier;
    this.value = value;
  }

  @Override
  public void render() {
    if (isPrimitive) {
      ImNodes.beginStaticAttribute(id);
    } else {
      ImNodes.beginOutputAttribute(id);
    }

    Snippets.drawTypeWithTooltip(typeName, tooltip);
    ImGui.sameLine();
    ImGui.textColored(Colors.Identifier, identifier);
    
    ImGui.sameLine();
    ImGui.text(value);

    if (isPrimitive) {
      ImNodes.endStaticAttribute();
    } else {
      ImNodes.endOutputAttribute();
    }
  }
}
