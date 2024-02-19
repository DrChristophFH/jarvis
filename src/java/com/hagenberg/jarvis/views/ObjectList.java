package com.hagenberg.jarvis.views;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.View;
import com.hagenberg.interaction.CommandRegistry;
import com.hagenberg.jarvis.models.InteractionState;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.wrappers.JArrayReference;
import com.hagenberg.jarvis.models.entities.wrappers.JContent;
import com.hagenberg.jarvis.models.entities.wrappers.JMember;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.models.entities.wrappers.JReferenceType;
import com.hagenberg.jarvis.models.entities.wrappers.JType;
import com.hagenberg.jarvis.models.entities.wrappers.JValue;

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
    CommandRegistry.getInstance().registerCommand(JObjectReference.class, (JObjectReference object) -> {
      iState.setSelectedObjectId(object.getObjectId());
    }, "Focus in Object List");
    CommandRegistry.getInstance().registerCommand(JReferenceType.class, (JReferenceType clazz) -> {
      currentFilter = clazz.getSimpleName();
      filterInput.set(currentFilter);
    }, "Filter in Object List");
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

    ImGui.sameLine();
    if (ImGui.button("Clear")) {
      filterInput.clear();
      currentFilter = null;
    }

    int tableFlags = ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg | ImGuiTableFlags.Reorderable | ImGuiTableFlags.Hideable
        | ImGuiTableFlags.ScrollX | ImGuiTableFlags.Resizable | ImGuiTableFlags.ScrollY;

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
      String typeName = object.type().getSimpleName();
      possibleFilters.add(typeName);
      if (currentFilter == null || currentFilter.equals(typeName)) {
        displayObject(object);
      }
    }
  }

  private void displayObject(JObjectReference object) {
    ImGui.tableNextRow();
    ImGui.tableNextColumn();
    // first column -> tree list
    int treeFlags = determineTreeFlags(object);

    if (iState.getSelectedObjectId() == object.getObjectId()) {
      ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg1, ImGui.getColorU32(ImGuiCol.TextSelectedBg));
    }

    boolean open = ImGui.treeNodeEx(object.name(), treeFlags);
    if (!open) {

      ImGui.pushID(object.name());
    }

    if (ImGui.beginPopupContextItem()) {
      // TODO Snippets.focusOnNode(object.getLayoutNode().getNodeId());
      ImGui.endPopup();
    }

    ImGui.tableNextColumn();
    Snippets.drawTypeWithTooltip(object.type(), tooltip);

    ImGui.tableNextColumn();
    // skip dynamic type

    ImGui.tableNextColumn();
    ImGui.text(object.getToString());

    if (open) {
      scaffoldObject(object);
      ImGui.treePop();
    } else {
      ImGui.popID();
    }
  }

  private void scaffoldObject(JObjectReference object) {
    for (JMember member : object.getMembers()) {
      displayElement(member.value(), member.field().name(), member.field().type());
    }
    if (object instanceof JArrayReference array) {
      JType elementType = array.getArrayContentType();
      for (JContent content : array.getContent()) {
        displayElement(content.value(), content.name(), elementType);
      }
    }
  }

  private void displayElement(JValue element, String name, JType type) {
    ImGui.tableNextRow();
    ImGui.tableNextColumn();

    int treeFlags = determineTreeFlags(element);

    boolean open = ImGui.treeNodeEx(name, treeFlags);
    if (!open) {
      // treenodeex does not push id if not open
      // to have rest of columns uniquely identified, we push id here
      ImGui.pushID(name);
    }

    ImGui.tableNextColumn();
    Snippets.drawTypeWithTooltip(type, tooltip);

    ImGui.tableNextColumn();
    if (element != null) {
      ImGui.pushID("dynamicType"); // else same id for static and dynamic type
      Snippets.drawTypeWithTooltip(element.type(), tooltip);
      ImGui.popID();
    }

    ImGui.tableNextColumn();
    String value = element == null ? "null" : element.getToString();
    ImGui.text(value);

    if (open) {
      if (element instanceof JObjectReference object) {
        scaffoldObject(object);
      }
      ImGui.treePop();
    } else {
      ImGui.popID();
    }
  }

  private int determineTreeFlags(JValue value) {
    int treeFlags = ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.SpanAvailWidth;
    if (value instanceof JArrayReference array) {
      if (array.getContent().isEmpty()) {
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
