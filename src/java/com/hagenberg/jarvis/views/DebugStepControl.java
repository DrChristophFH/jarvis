package com.hagenberg.jarvis.views;

import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.debugger.StepCommand;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class DebugStepControl extends View {

  private CommandExecutor executor;

  public interface CommandExecutor {
    void executeCommand(StepCommand command);
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
    if (ImGui.button("Step Over")) executor.executeCommand(StepCommand.STEP_OVER);
    ImGui.sameLine();
    if (ImGui.button("Step Into")) executor.executeCommand(StepCommand.STEP_INTO);
    ImGui.sameLine();
    if (ImGui.button("Step Out")) executor.executeCommand(StepCommand.STEP_OUT);
    ImGui.sameLine();
    if (ImGui.button("Resume")) executor.executeCommand(StepCommand.RESUME);
    ImGui.sameLine();
    if (ImGui.button("Stop")) executor.executeCommand(StepCommand.STOP);
  }
}
