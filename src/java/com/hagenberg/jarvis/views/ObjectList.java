package com.hagenberg.jarvis.views;

import java.util.List;

import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.InteractionState;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTableBgTarget;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiTreeNodeFlags;

public class ObjectList extends View {

  private InteractionState iState;
  private ObjectGraphModel model;

  public ObjectList(InteractionState interactionState) {
    setName("Object List");
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

      showObjectsTable(model.getObjects());

      // End the table
      ImGui.endTable();
    }
  }

  private void showObjectsTable(List<ObjectGNode> objects) {
    for (ObjectGNode object : objects) {
      displayObject(object);
    }
  }

  private void displayObject(ObjectGNode object) {
    ImGui.tableNextRow();
    ImGui.tableNextColumn();

    int treeFlags = ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.SpanAvailWidth;

    if (object.getMembers().size() == 0) {
      treeFlags |= ImGuiTreeNodeFlags.Leaf;
    }

    if (iState.getSelectedObjectId() == object.getId()) {
      ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg1, ImGui.getColorU32(ImGuiCol.TextSelectedBg));
    }

    boolean open = ImGui.treeNodeEx("Object#" + object.getId(), treeFlags);

    ImGui.tableNextColumn();
    ImGui.text(object.getType());
    ImGui.tableNextColumn();
    ImGui.textDisabled("--");
    if (open) {
      for (MemberGVariable member : object.getMembers()) {
        if (member.getNode() instanceof ObjectGNode memberObject) {
          displayObject(memberObject);
        } else if (member.getNode() instanceof PrimitiveGNode memberPrimitive) {
          displayPrimitive(member.getName(), memberPrimitive);
        }
      }
      ImGui.treePop();
    }
  }

  private void displayPrimitive(String name, PrimitiveGNode memberPrimitive) {
    ImGui.tableNextRow();
    ImGui.tableNextColumn();
    ImGui.text(name);
    ImGui.tableNextColumn();
    ImGui.text(memberPrimitive.getType());
    ImGui.tableNextColumn();
    ImGui.text(memberPrimitive.getPrimitiveValue().toString());
  }
}
