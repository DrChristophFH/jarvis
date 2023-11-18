package com.hagenberg.jarvis.views;

import java.util.List;

import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.CallStackModel;
import com.hagenberg.jarvis.models.entities.CallStackFrame;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;

import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;

public class CallStack extends View {

  private CallStackModel model = new CallStackModel();
  private boolean fullClassName = false;

  public CallStack() {
    setName("Call Stack");
    setFlags(ImGuiWindowFlags.HorizontalScrollbar | ImGuiWindowFlags.MenuBar);
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

  private void showParameters(List<LocalGVariable> parameters) {
    for (LocalGVariable parameter : parameters) {
      ImGui.tableNextRow();
      ImGui.tableNextColumn();
      ImGui.text(parameter.getName());
      // Context menu for each row
      int nodeId = parameter.getLayoutNode().getNodeId();
      if (ImGui.beginPopupContextItem("parameterContextMenu" + nodeId)) {
        Snippets.focusOnNode(nodeId);
        ImGui.endPopup();
      }
      ImGui.tableNextColumn();
      Snippets.drawTypeWithTooltip(parameter.getStaticTypeName(), tooltip);
      ImGui.tableNextColumn();
      Snippets.drawTypeWithTooltip(parameter.getNode().getTypeName(), tooltip);
      ImGui.tableNextColumn();
      if (parameter.getNode() instanceof ObjectGNode object) {
        ImGui.text("Object#%s = %s".formatted(object.getObjectId(), object.getToString()));
      } else if (parameter.getNode() instanceof PrimitiveGNode primitive) {
        ImGui.text(primitive.getPrimitiveValue().toString());
      }
    }
  }
}
