package com.hagenberg.jarvis.graph.render.attributes;

import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.graph.transform.TransformerRegistry;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;

import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;

public abstract class Attribute {
  protected final int attId;
  protected final Node parent;
  
  public Attribute(int id, Node parent) {
    this.attId = id;
    this.parent = parent;
  }

  public int getAttId() {
    return attId;
  }

  public Node getParent() {
    return parent;
  }

  public abstract void render();

  protected void memberContextMenu(TransformerRegistry registry, MemberGVariable member) {
    if (ImGui.isItemHovered() && ImGui.isMouseReleased(ImGuiMouseButton.Right)) {
      ImGui.openPopup("MemCtx##" + attId);
    }

    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5); // NodeEditor somehow overrides this so we have to set it here
    if (ImGui.beginPopup("MemCtx##" + attId)) {
      ImGui.menuItem("Settings for " + member.getName(), "", false, false);
      if (member.getNode() instanceof PrimitiveGNode prim) {
        if (ImGui.beginMenu("Renderer")) {
          ImGui.endMenu();
        }
      }
      ImGui.endPopup();
    }
    ImGui.popStyleVar();
  }
}
