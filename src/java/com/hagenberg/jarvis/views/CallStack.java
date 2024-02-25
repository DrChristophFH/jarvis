package com.hagenberg.jarvis.views;

import java.util.List;

import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.CallStackModel;
import com.hagenberg.jarvis.models.ClassModel;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.CallStackFrame;
import com.hagenberg.jarvis.models.entities.wrappers.JLocalVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.models.entities.wrappers.JPrimitiveValue;

import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;

public class CallStack extends View {

  private CallStackModel model;
  private boolean fullClassName = false;

  public CallStack(ObjectGraphModel objectGraphModel, ClassModel classModel) {
    setName("Call Stack");
    setFlags(ImGuiWindowFlags.HorizontalScrollbar | ImGuiWindowFlags.MenuBar);
    model = new CallStackModel(objectGraphModel, classModel);
  }

  public CallStackModel getCallStackModel() {
    return model;
  }

  @Override
  protected void renderWindow() {
    if (model.getCallStack().isEmpty()) {
      ImGui.text("No call stack available.");
      return;
    }

    MenuBar();

    for (CallStackFrame frame : model.getCallStack()) {
      String method = fullClassName ? frame.getFullMethodHeader() : frame.getSimpleMethodHeader();
      String call = "%s : %s".formatted(method, frame.getLineNumber());
      if (ImGui.collapsingHeader(call)) {
        int tableFlags = ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg | ImGuiTableFlags.Resizable | ImGuiTableFlags.Reorderable | ImGuiTableFlags.Hideable;

        if (ImGui.beginTable("callStack", 4, tableFlags)) {

          // Setup the table columns
          ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.NoHide);
          ImGui.tableSetupColumn("Static Type");
          ImGui.tableSetupColumn("Dynamic Type");
          ImGui.tableSetupColumn("Value");
          ImGui.tableHeadersRow();

          showParameters(frame.getParameters());

          // End the table
          ImGui.endTable();
        }
      }
    }
  }

  private void MenuBar() {
    if (ImGui.beginMenuBar()) {
      if (ImGui.beginMenu("Options")) {
        if (ImGui.checkbox("Full Class Name", fullClassName)) {
          fullClassName = !fullClassName;
        }
        ImGui.endMenu();
      }
      ImGui.endMenuBar();
    }
  }

  private void showParameters(List<JLocalVariable> parameters) {
    for (JLocalVariable parameter : parameters) {
      ImGui.tableNextRow();
      ImGui.tableNextColumn();
      ImGui.text(parameter.name());
      // Context menu for each row
      // int nodeId = 0; // TODO parameter.getLayoutNode().getNodeId();
      // if (ImGui.beginPopupContextItem("parameterContextMenu" + nodeId)) {
      //   Snippets.focusOnNode(nodeId);
      //   ImGui.endPopup();
      // }
      ImGui.pushID(parameter.name());

      ImGui.tableNextColumn();
      Snippets.drawTypeWithTooltip(parameter.getType(), tooltip);

      ImGui.tableNextColumn();
      ImGui.pushID("dynamicType"); 
      Snippets.drawTypeWithTooltip(parameter.value().type(), tooltip);
      ImGui.popID();

      ImGui.tableNextColumn();
      if (parameter.value() instanceof JObjectReference object) {
        ImGui.text("Object#%s = %s".formatted(object.getObjectId(), object.getToString()));
      } else if (parameter.value() instanceof JPrimitiveValue primitive) {
        ImGui.text(primitive.getJdiPrimitiveValue().toString());
      }
      ImGui.popID();
    }
  }
}
