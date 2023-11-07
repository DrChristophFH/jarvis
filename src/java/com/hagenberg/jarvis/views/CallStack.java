package com.hagenberg.jarvis.views;

import java.util.List;

import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.CallStackModel;
import com.hagenberg.jarvis.models.entities.CallStackFrame;
import com.hagenberg.jarvis.models.entities.graph.GNode;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;
import com.hagenberg.jarvis.util.Snippets;
import com.hagenberg.jarvis.util.TypeFormatter;

import imgui.ImGui;
import imgui.flag.ImGuiSelectableFlags;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;

public class CallStack extends View {

  private CallStackModel model = new CallStackModel();

  public CallStack() {
    setName("Call Stack");
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

    for (CallStackFrame frame : model.getCallStack()) {
      String call = "%s : %s".formatted(frame.getFullMethodHeader(), frame.getLineNumber());
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

  private void showParameters(List<LocalGVariable> parameters) {
    for (LocalGVariable parameter : parameters) {
      ImGui.tableNextRow();
      ImGui.tableNextColumn();
      ImGui.text(parameter.getName());
      // Context menu for each row
      if (ImGui.beginPopupContextItem("parameterContextMenu" + parameter.getNodeId())) {
        Snippets.focusOnNode(parameter.getNodeId());
        ImGui.endPopup();
      }
      ImGui.tableNextColumn();
      Snippets.drawTypeWithTooltip(parameter.getStaticType());
      ImGui.tableNextColumn();
      Snippets.drawTypeWithTooltip(parameter.getNode().getType());
      ImGui.tableNextColumn();
      if (parameter.getNode() instanceof ObjectGNode object) {
        ImGui.text("Object#%s = %s".formatted(object.getObjectId(), object.getToString()));
      } else if (parameter.getNode() instanceof PrimitiveGNode primitive) {
        ImGui.text(primitive.getPrimitiveValue().toString());
      }
    }
  }
}
