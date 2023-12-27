package com.hagenberg.jarvis.views;

import java.util.List;

import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.InteractionState;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.wrappers.JArrayReference;
import com.hagenberg.jarvis.models.entities.wrappers.JField;
import com.hagenberg.jarvis.models.entities.wrappers.JLocalVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.models.entities.wrappers.JType;
import com.hagenberg.jarvis.models.entities.wrappers.JValue;

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

    int tableFlags = ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg | ImGuiTableFlags.Reorderable | ImGuiTableFlags.Hideable | ImGuiTableFlags.ScrollX | ImGuiTableFlags.SizingFixedFit | ImGuiTableFlags.ScrollY;

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

  private void showLocalVarsTable(List<JLocalVariable> localVars) {
    for (JLocalVariable localVar : localVars) {
      displayElement(localVar.value(), localVar.name(), localVar.getType());
    }
  }

  private void displayElement(JValue value, String name, JType type) {
    ImGui.tableNextRow();
    ImGui.tableNextColumn();

    int treeFlags = determineTreeFlags(value);

    boolean open = ImGui.treeNodeEx(name, treeFlags);

    ImGui.tableNextColumn();
    Snippets.drawTypeWithTooltip(type, tooltip);
    ImGui.tableNextColumn();
    Snippets.drawTypeWithTooltip(value.type(), tooltip);
    ImGui.tableNextColumn();

    ImGui.text(value.toString());

    if (open) {
      if (value instanceof JObjectReference object) {
        scaffoldObject(object);
      }
      ImGui.treePop();
    }
  }

  private void scaffoldObject(JObjectReference object) {
    for (JField field : object.getMembers().keySet()) {
      displayElement(object.getMember(field), field.name(), field.type());
    }
    if (object instanceof JArrayReference array) {
      int index = 0;
      JType elementType = array.type();
      for (JValue element : array.getValues()) {
        displayElement(element, "[" + index + "]", elementType);
        index++;
      }
    }
  }

  private int determineTreeFlags(JValue value) {
    int treeFlags = ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.SpanAvailWidth;
    if (value instanceof JArrayReference array) {
      if (array.getValues().isEmpty()) {
        treeFlags |= ImGuiTreeNodeFlags.Leaf;
      }
    } else if (value instanceof JObjectReference object) {
      if (object.getMembers().isEmpty()) {
        treeFlags |= ImGuiTreeNodeFlags.Leaf;
      }
    } else {
      treeFlags |= ImGuiTreeNodeFlags.Leaf;
    }
    return treeFlags;
  }
}
