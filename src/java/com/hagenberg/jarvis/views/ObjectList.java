package com.hagenberg.jarvis.views;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.InteractionState;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.graph.ContentGVariable;
import com.hagenberg.jarvis.models.entities.graph.GVariable;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JArrayReference;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.models.entities.wrappers.JValue;
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

    int tableFlags = ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg | ImGuiTableFlags.Reorderable | ImGuiTableFlags.Hideable
        | ImGuiTableFlags.ScrollX | ImGuiTableFlags.SizingFixedFit | ImGuiTableFlags.ScrollY;

    if (ImGui.beginTable("table", 4, tableFlags)) {

      // Setup the table columns
      ImGui.tableSetupScrollFreeze(1, 1);
      ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.NoHide);
      ImGui.tableSetupColumn("Static Type");
      ImGui.tableSetupColumn("Dynamic Type");
      ImGui.tableSetupColumn("Value");
      ImGui.tableHeadersRow();

      showObjectsTable(model.getObjects());

      // End the table
      ImGui.endTable();
    }
  }

  private void showObjectsTable(List<JObjectReference> objects) {
    possibleFilters.clear();
    for (JObjectReference object : objects) {
      String typeName = TypeFormatter.getSimpleType(object.getTypeName());
      possibleFilters.add(typeName);
      if (currentFilter == null || currentFilter.equals(typeName)) {
        displayObject(object);
      }
    }
  }

  private void displayObject(JObjectReference object) {
    ImGui.tableNextRow();
    ImGui.tableNextColumn();

    int treeFlags = ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.SpanAvailWidth;

    if (object instanceof JArrayReference array) {
      if (array.getValues().isEmpty()) {
        treeFlags |= ImGuiTreeNodeFlags.Leaf;
      }
    } else if (object.getMembers().size() == 0) {
      treeFlags |= ImGuiTreeNodeFlags.Leaf;
    }

    if (iState.getSelectedObjectId() == object.getObjectId()) {
      ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg1, ImGui.getColorU32(ImGuiCol.TextSelectedBg));
    }

    boolean open = ImGui.treeNodeEx(object.toString(), treeFlags);

    if (ImGui.beginPopupContextItem()) {
      // TODO Snippets.focusOnNode(object.getLayoutNode().getNodeId());
      ImGui.endPopup();
    }

    ImGui.tableNextColumn();
    Snippets.drawTypeWithTooltip(object.getTypeName(), tooltip);
    ImGui.tableNextColumn();
    // skip dynamic type
    ImGui.tableNextColumn();
    ImGui.text(object.getToString());
    if (open) {
      for (MemberGVariable member : object.getMembers()) {
        displayVariable(member);
      }
      if (object instanceof JArrayReference array) {
        for (ContentGVariable element : array.getValues()) {
          displayVariable(element);
        }
      }
      ImGui.treePop();
    }
  }

  private void displayVariable(GVariable variable) {
    ImGui.tableNextRow();
    ImGui.tableNextColumn();

    int treeFlags = ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.SpanAvailWidth;

    JValue node = variable.value();
    String typeName = node == null ? "null" : node.getTypeName();
    String toString = node == null ? "null" : node.getToString();

    if (node instanceof JArrayReference array) {
      if (array.getValues().isEmpty()) {
        treeFlags |= ImGuiTreeNodeFlags.Leaf;
      }
    } else if (node instanceof JObjectReference object) {
      if (object.getMembers().isEmpty()) {
        treeFlags |= ImGuiTreeNodeFlags.Leaf;
      }
    } else {
      treeFlags |= ImGuiTreeNodeFlags.Leaf;
    }

    boolean open = ImGui.treeNodeEx(variable.name(), treeFlags);

    ImGui.tableNextColumn();
    Snippets.drawTypeWithTooltip(variable.getStaticTypeName(), tooltip);
    ImGui.tableNextColumn();
    Snippets.drawTypeWithTooltip(typeName, tooltip);
    ImGui.tableNextColumn();

    ImGui.text(toString);

    if (open) {
      if (node instanceof JObjectReference object) {
        for (MemberGVariable member : object.getMembers()) {
          displayVariable(member);
        }
        if (object instanceof JArrayReference array) {
          for (ContentGVariable element : array.getValues()) {
            displayVariable(element);
          }
        }
      }
      ImGui.treePop();
    }
  }
}
