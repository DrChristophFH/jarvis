package com.hagenberg.jarvis.views;

import java.util.List;

import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.CallStackModel;
import com.hagenberg.jarvis.models.entities.CallStackFrame;
import com.hagenberg.jarvis.models.entities.graph.GNode;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;

import imgui.ImGui;
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
      String call = "%s : %s %s".formatted(frame.getMethodName(), frame.getClassName(), frame.getLineNumber());
      if (ImGui.collapsingHeader(call)) {
        int tableFlags = ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg | ImGuiTableFlags.Resizable | ImGuiTableFlags.Reorderable
            | ImGuiTableFlags.Hideable;

        if (ImGui.beginTable("localVarList", 3, tableFlags)) {

          // Setup the table columns
          ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.NoHide);
          ImGui.tableSetupColumn("Type");
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
      ImGui.tableNextColumn();
      ImGui.text(parameter.getStaticType()); 
      ImGui.tableNextColumn();
      GNode gNode = parameter.getNode();

      if (gNode instanceof PrimitiveGNode primitive) {
        ImGui.text(primitive.getPrimitiveValue().toString());
      } else {
        ObjectGNode object = (ObjectGNode) gNode;
        String name = "Object#%s = %s".formatted(object.getObjectId(), object.getToString());
        ImGui.text(name);
      }
    }
  }
}
