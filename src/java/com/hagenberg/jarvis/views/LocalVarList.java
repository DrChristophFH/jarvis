package com.hagenberg.jarvis.views;

import java.util.List;

import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.InteractionState;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;

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

    int tableFlags = ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg | ImGuiTableFlags.Resizable
        | ImGuiTableFlags.Reorderable | ImGuiTableFlags.Hideable;

    if (ImGui.beginTable("table", 3, tableFlags)) {

      // Setup the table columns
      ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.NoHide);
      ImGui.tableSetupColumn("Type");
      ImGui.tableSetupColumn("Value");
      ImGui.tableHeadersRow();

      showLocalVarsTable(model.getLocalVars());

      // End the table
      ImGui.endTable();
    }
  }

  private void showLocalVarsTable(List<LocalGVariable> localVars) {
    for (LocalGVariable localVar : localVars) {
      ImGui.tableNextRow();
      ImGui.tableNextColumn();
      ImGui.text(localVar.getName());
      ImGui.tableNextColumn();
      ImGui.text(localVar.getNode().getType()); // TODO static vs dynamic type (this is dynamic)
      ImGui.tableNextColumn();
      if (localVar.getNode() instanceof ObjectGNode object) {
        if (ImGui.selectable("Object#" + object.getId(), iState.getSelectedObjectId() == object.getId(),
            ImGuiSelectableFlags.SpanAllColumns)) {
          iState.setSelectedObjectId(object.getId());
        }
      } else if (localVar.getNode() instanceof PrimitiveGNode primitive) {
        ImGui.text(primitive.getPrimitiveValue().toString());
      }
    }
  }
}