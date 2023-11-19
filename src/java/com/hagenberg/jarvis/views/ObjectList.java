package com.hagenberg.jarvis.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.InteractionState;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;
import com.hagenberg.jarvis.util.TypeFormatter;

import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTableBgTarget;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;

public class ObjectList extends View {

  private InteractionState iState;
  private ObjectGraphModel model;
  private ImString filterInput = new ImString(256);
  private String currentFilter = null;
  Set<String> possibleFilters = new HashSet<>();

  private ImGuiInputTextCallback textEditCallback = new ImGuiInputTextCallback() {
    @Override
    public void accept(ImGuiInputTextCallbackData data) {
      String currentInput = data.getBuf();

      // Iterate over possible filters and find matches
      for (String filter : possibleFilters) {
        if (filter.startsWith(currentInput)) {
          data.deleteChars(0, currentInput.length());
          data.insertChars(0, filter);
          data.setCursorPos(filter.length());
          break;
        }
      }
    }
  };

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

    int inputFlags = ImGuiInputTextFlags.CallbackCompletion | ImGuiInputTextFlags.EnterReturnsTrue;

    if (ImGui.inputText("Filter", filterInput, inputFlags, textEditCallback)) {
      if (!possibleFilters.contains(filterInput.get())) {
        filterInput.clear();
        currentFilter = null;
      } else {
        currentFilter = filterInput.get();
      }
    }

    int tableFlags = ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg | ImGuiTableFlags.Reorderable | ImGuiTableFlags.Hideable | ImGuiTableFlags.ScrollX | ImGuiTableFlags.SizingFixedFit | ImGuiTableFlags.ScrollY;

    if (ImGui.beginTable("table", 3, tableFlags)) {

      // Setup the table columns
      ImGui.tableSetupScrollFreeze(1, 1);
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
    possibleFilters.clear();
    for (ObjectGNode object : objects) {
      String typeName = TypeFormatter.getSimpleType(object.getTypeName());
      possibleFilters.add(typeName);
      if (currentFilter == null || currentFilter.equals(typeName)) {
        displayObject(object);
      }
    }
  }

  private void displayObject(ObjectGNode object) {
    ImGui.tableNextRow();
    ImGui.tableNextColumn();

    int treeFlags = ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.SpanAvailWidth;

    if (object.getMembers().size() == 0) {
      treeFlags |= ImGuiTreeNodeFlags.Leaf;
    }

    if (iState.getSelectedObjectId() == object.getObjectId()) {
      ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg1, ImGui.getColorU32(ImGuiCol.TextSelectedBg));
    }

    boolean open = ImGui.treeNodeEx("Object#" + object.getObjectId(), treeFlags);

    if (ImGui.beginPopupContextItem()) {
      Snippets.focusOnNode(object.getLayoutNode().getNodeId());
      ImGui.endPopup();
    }

    ImGui.tableNextColumn();
    Snippets.drawTypeWithTooltip(object.getTypeName(), tooltip);
    ImGui.tableNextColumn();
    ImGui.text(object.getToString());
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
    int treeFlags = ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf;
    if (ImGui.treeNodeEx(name, treeFlags)) {
      ImGui.treePop();
    }
    ImGui.tableNextColumn();
    Snippets.drawTypeWithTooltip(memberPrimitive.getTypeName(), tooltip);
    ImGui.tableNextColumn();
    ImGui.text(memberPrimitive.getPrimitiveValue().toString());
  }
}
