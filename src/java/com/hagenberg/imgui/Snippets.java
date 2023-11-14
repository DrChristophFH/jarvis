package com.hagenberg.imgui;

import java.util.List;

import com.hagenberg.imgui.components.Tooltip;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.graph.rendering.renderers.Renderer;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imnodes.ImNodes;
import imgui.flag.ImGuiMouseButton;
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

  public static String getSimpleType(String typeName) {
    return typeName.substring(typeName.lastIndexOf(".") + 1);
  }

  public static void drawTypeWithTooltip(String typeName, Tooltip tooltip) {
    ImGui.textColored(Colors.Type, getSimpleType(typeName));
    tooltip.show(() -> {
      ImGui.text(typeName);
    });
  }

  public static void memberContextMenu(int attId, MemberGVariable member, RendererRegistry registry) {
    if (ImGui.isItemHovered() && ImGui.isMouseReleased(ImGuiMouseButton.Right)) {
      ImGui.openPopup("MemCtx##" + attId);
    }

    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5); // NodeEditor somehow overrides this so we have to set it here
    if (ImGui.beginPopup("MemCtx##" + attId)) {
      ImGui.menuItem("Settings for " + member.getName(), "", false, false);
      if (member.getNode() instanceof PrimitiveGNode prim) {
        if (ImGui.beginMenu("Renderer")) {
          List<Renderer<PrimitiveGNode>> renderers = registry.getApplicableRenderers(prim);
          Renderer<PrimitiveGNode> currentRenderer = registry.getPrimitiveRenderer(member);
          for (Renderer<PrimitiveGNode> renderer : renderers) {
            if (ImGui.menuItem(renderer.getName(), "", renderer == currentRenderer)) {
              registry.setFieldRenderer(member.getField(), renderer);
            }
          }
          if (ImGui.menuItem("[Reset to Default]")) {
            registry.setFieldRenderer(member.getField(), null);
          }
          ImGui.endMenu();
        }
      }
      ImGui.endPopup();
    }
    ImGui.popStyleVar();
  }
}
