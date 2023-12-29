package com.hagenberg.imgui;

import java.util.function.Consumer;

import com.hagenberg.imgui.components.Tooltip;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.models.entities.wrappers.JType;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imnodes.ImNodes;

public class Snippets {

  public static void focusOnNode(int nodeId) {
    if (ImGui.selectable("Focus in Object Graph")) {
      ImNodes.editorMoveToNode(nodeId);
      ImVec2 nodePos = new ImVec2();
      ImNodes.editorContextGetPanning(nodePos);
      ImNodes.editorResetPanning(nodePos.x + 400, nodePos.y + 400);
    }
  }

  public static void forSelectedNodes(Consumer<Integer> consumer) {
    int count = ImNodes.numSelectedNodes();
    if (count > 0) {
      int[] selectedNodes = new int[count];
      ImNodes.getSelectedNodes(selectedNodes);
      for (int nodeId : selectedNodes) {
        consumer.accept(nodeId);
      }
    }
  }
  
  public static void drawTypeWithTooltip(JType type, Tooltip tooltip) {
    if (type == null) {
      ImGui.text("[not loaded]");
      return;
    }
    ImGui.textColored(Colors.Type, type.getSimpleName());
    tooltip.show(() -> {
      ImGui.text(type.name());
    });
  }

  public static void DisplayLayoutNodeDebug(Node layoutNode) {
    ImGui.text("Id: " + layoutNode);
    ImGui.text("Position: " + layoutNode.getPosition());
    ImGui.text("Velocity: " + layoutNode.getVelocity());
    ImGui.text("Width: " + layoutNode.getWidth());
  }
}
