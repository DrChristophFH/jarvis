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
  public void render() {
    ImNodes.pushColorStyle(ImNodesColorStyle.TitleBar, Colors.LocalVariable);
    ImNodes.beginNode(nodeId);

    ImNodes.setNodeDraggable(nodeId, true);
    ImNodes.setNodeGridSpacePos(nodeId, position.x, position.y);
    length = (int) ImNodes.getNodeDimensionsX(nodeId);

    ImNodes.beginNodeTitleBar();
    ImGui.text("Local Variable: " + variableName);
    ImNodes.endNodeTitleBar();

    ImNodes.popColorStyle();

    for (Attribute attribute : attributes) {
      attribute.render();
    }

    ImNodes.endNode();
  }
}
