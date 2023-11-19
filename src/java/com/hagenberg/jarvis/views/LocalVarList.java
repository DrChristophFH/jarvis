package com.hagenberg.jarvis.views;

import java.util.List;

import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.InteractionState;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.graph.ArrayGNode;
import com.hagenberg.jarvis.models.entities.graph.ContentGVariable;
import com.hagenberg.jarvis.models.entities.graph.GNode;
import com.hagenberg.jarvis.models.entities.graph.GVariable;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;

import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiTreeNodeFlags;

public class LocalVarList extends View {

  private InteractionState iState;
  private ObjectGraphModel model;

  public LocalVarList(InteractionState interactionState) {
    setName("Local Variable List");
    this.iState = interactionState;
  }

  public void setObjectGraphModel(ObjectGraphModel model) {
    this.model = model;
  }

  @Override
  protected void renderWindow() {
    if (model == null) {
      ImGui.text("No object graph model available");
      return;
    }

    int tableFlags = 
      ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg | ImGuiTableFlags.Reorderable | 
      ImGuiTableFlags.Hideable | ImGuiTableFlags.ScrollX | ImGuiTableFlags.SizingFixedFit | 
      ImGuiTableFlags.ScrollY;

    if (ImGui.beginTable("localVarList", 4, tableFlags)) {

      // Setup the table columns
      ImGui.tableSetupScrollFreeze(1, 1);
      ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.NoHide);
      ImGui.tableSetupColumn("Static Type");
      ImGui.tableSetupColumn("Dynamic Type");
      ImGui.tableSetupColumn("Value");
      ImGui.tableHeadersRow();

      showLocalVarsTable(model.getLocalVariables());

      // End the table
      ImGui.endTable();
    }
  }

  private void showLocalVarsTable(List<LocalGVariable> localVars) {
    for (LocalGVariable localVar : localVars) {
      displayVariable(localVar);
    }
  }

  private void displayVariable(GVariable variable) {
    ImGui.tableNextRow();
    ImGui.tableNextColumn();

    int treeFlags = ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.SpanAvailWidth;

    GNode node = variable.getNode();
    String typeName = node == null ? "null" : node.getTypeName();
    String toString = node == null ? "null" : node.getToString();

    if (node instanceof ObjectGNode object) {
      if (object.getMembers().size() == 0) {
        treeFlags |= ImGuiTreeNodeFlags.Leaf;
      }
    } else if (node instanceof PrimitiveGNode) {
      treeFlags |= ImGuiTreeNodeFlags.Leaf;
    }

    boolean open = ImGui.treeNodeEx(variable.getName(), treeFlags);

    ImGui.tableNextColumn();
    Snippets.drawTypeWithTooltip(variable.getStaticTypeName(), tooltip);
    ImGui.tableNextColumn();
    Snippets.drawTypeWithTooltip(typeName, tooltip);
    ImGui.tableNextColumn();

    ImGui.text(toString);

    if (open) {
      if (node instanceof ObjectGNode object) {
        for (MemberGVariable member : object.getMembers()) {
          displayVariable(member);
        }
        if (object instanceof ArrayGNode array) {
          for (ContentGVariable element : array.getContentGVariables()) {
            displayVariable(element);
          }
        }
      }
      ImGui.treePop();
    }
  }
}
