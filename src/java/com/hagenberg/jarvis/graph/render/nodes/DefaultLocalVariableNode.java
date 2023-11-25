package com.hagenberg.jarvis.graph.render.nodes;

import com.hagenberg.imgui.Colors;
import com.hagenberg.jarvis.graph.render.attributes.Attribute;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.flag.ImNodesColorStyle;

public class DefaultLocalVariableNode extends Node {

  private final String variableName;

  public DefaultLocalVariableNode(int nodeId, String variableName) {
    super(nodeId);
    this.variableName = variableName;
  }

  @Override
  protected void preNode() {
    ImNodes.pushColorStyle(ImNodesColorStyle.TitleBar, Colors.LocalVariable);
  }

  @Override
  protected void headerContent() {
    ImGui.text("Local Variable: " + variableName);
  }

  @Override
  protected void content() {
    for (Attribute attribute : attributes) {
      attribute.render();
    }
  }

  @Override
  protected void postNode() {
    ImNodes.popColorStyle();
  }
}
