package com.hagenberg.imgui;

import java.util.function.Consumer;

import com.hagenberg.imgui.components.Tooltip;
import com.hagenberg.interaction.Command;
import com.hagenberg.interaction.CommandRegistry;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.models.entities.wrappers.JReferenceType;
import com.hagenberg.jarvis.models.entities.wrappers.JType;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imnodes.ImNodes;
import imgui.flag.ImGuiStyleVar;

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

    if (type instanceof JReferenceType jRefType) {
      ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);
      if (ImGui.beginPopupContextItem("" + ImGui.getID("TypeCtx"))) {
        for (Command<JReferenceType> command : CommandRegistry.getInstance().getCommandsForObject(JReferenceType.class)) {
          if (ImGui.menuItem(command.getName(), "", false, true)) {
            command.execute(jRefType);
          }
        }
        ImGui.endPopup();
      }
      ImGui.popStyleVar();
    }

    tooltip.show(() -> {
      ImGui.text(type.name());
    });
  }

  public static void drawHelpMarker(String desc) {
    ImGui.textDisabled("(?)");
    if (ImGui.isItemHovered()) {
      ImGui.beginTooltip();
      ImGui.pushTextWrapPos(450);
      ImGui.text(desc);
      ImGui.popTextWrapPos();
      ImGui.endTooltip();
    }
  }

  public static void DisplayLayoutNodeDebug(Node layoutNode) {
    ImGui.text("Id: " + layoutNode);
    ImGui.text("Position: " + layoutNode.getPosition());
    ImGui.text("Velocity: " + layoutNode.getVelocity());
    ImGui.text("Width: " + layoutNode.getWidth());
  }
}
