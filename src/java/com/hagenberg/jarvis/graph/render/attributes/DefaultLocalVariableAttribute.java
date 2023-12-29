package com.hagenberg.jarvis.graph.render.attributes;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.components.Tooltip;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.models.entities.wrappers.JType;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;

public class DefaultLocalVariableAttribute extends Attribute {

  private final boolean isPrimitive;
  private final JType type;
  private final String identifier;
  private final String value;
  private final Tooltip tooltip = new Tooltip();

  public DefaultLocalVariableAttribute(int id, Node parent, boolean isPrimitive, JType type, String identifier, String value) {
    super(id, parent);
    this.isPrimitive = isPrimitive;
    this.type = type;
    this.identifier = identifier;
    this.value = value;
  }

  @Override
  public void render() {
    if (isPrimitive) {
      ImNodes.beginStaticAttribute(attId);
    } else {
      ImNodes.beginOutputAttribute(attId);
    }

    Snippets.drawTypeWithTooltip(type, tooltip);
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
