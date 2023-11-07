package com.hagenberg.jarvis.views;

import java.util.List;

import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.InteractionState;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;
import com.hagenberg.jarvis.util.Snippets;
import imgui.ImGui;
import imgui.flag.ImGuiSelectableFlags;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;

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

    int tableFlags = ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg | ImGuiTableFlags.Resizable | ImGuiTableFlags.Reorderable | ImGuiTableFlags.Hideable;

    if (ImGui.beginTable("localVarList", 4, tableFlags)) {

      // Setup the table columns
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
      ImGui.tableNextRow();
      ImGui.tableNextColumn();
      ImGui.text(localVar.getName());
      // Context menu for each row
      if (ImGui.beginPopupContextItem("localVarContextMenu" + localVar.getNodeId())) {
        Snippets.focusOnNode(localVar.getNodeId());
        ImGui.endPopup();
      }
      ImGui.tableNextColumn();
      Snippets.drawTypeWithTooltip(localVar.getStaticType());
      ImGui.tableNextColumn();
      Snippets.drawTypeWithTooltip(localVar.getNode().getType());
      ImGui.tableNextColumn();
      if (localVar.getNode() instanceof ObjectGNode object) {
        String name = "Object#%s = %s".formatted(object.getObjectId(), object.getToString());
        if (ImGui.selectable(name, iState.getSelectedObjectId() == object.getObjectId(), ImGuiSelectableFlags.SpanAllColumns)) {
          iState.setSelectedObjectId(object.getObjectId());
        }
      } else if (localVar.getNode() instanceof PrimitiveGNode primitive) {
        ImGui.text(primitive.getPrimitiveValue().toString());
      }
    }
  }
}
