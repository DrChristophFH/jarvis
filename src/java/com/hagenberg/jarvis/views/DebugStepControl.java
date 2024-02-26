package com.hagenberg.jarvis.views;

import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.debugger.StepCommand;
import com.sun.jdi.request.EventRequest;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class DebugStepControl extends View {

  private CommandExecutor executor;
  private int suspendPolicy = EventRequest.SUSPEND_ALL;

  public interface CommandExecutor {
    void executeCommand(StepCommand command, int suspendPolicy);
  }

  public DebugStepControl() {
    setName("Debug Step Control");
    setFlags(ImGuiWindowFlags.NoNav | ImGuiWindowFlags.AlwaysAutoResize);
  }

  public void setCommandExecutor(CommandExecutor executor) {
    this.executor = executor;
  }

  @Override
  public void render() {
    super.render();
  }

  @Override
  protected void renderWindow() {
    if (executor == null) { 
      ImGui.text("No executor set!");
      return;
    }
    if (ImGui.button("Step Over")) executor.executeCommand(StepCommand.STEP_OVER, suspendPolicy);
    ImGui.sameLine();
    if (ImGui.button("Step Into")) executor.executeCommand(StepCommand.STEP_INTO, suspendPolicy);
    ImGui.sameLine();
    if (ImGui.button("Step Out")) executor.executeCommand(StepCommand.STEP_OUT, suspendPolicy);
    ImGui.sameLine();
    if (ImGui.button("Resume")) executor.executeCommand(StepCommand.RESUME, suspendPolicy);
    ImGui.sameLine();
    if (ImGui.button("Stop")) executor.executeCommand(StepCommand.STOP, suspendPolicy);
    ImGui.sameLine();
    if (ImGui.checkbox("Suspend All", suspendPolicy == EventRequest.SUSPEND_ALL)) {
      suspendPolicy = suspendPolicy == EventRequest.SUSPEND_ALL ? EventRequest.SUSPEND_EVENT_THREAD : EventRequest.SUSPEND_ALL;
    }
  }
}
