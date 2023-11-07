package com.hagenberg.jarvis.views;

import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.graph.GraphLayouter;

import imgui.ImGui;

public class LayouterControl extends View {

  private GraphLayouter layouter;

  public LayouterControl(GraphLayouter layouter) {
    this.layouter = layouter;
    setName("Layouter Control");
  }

  @Override
  protected void renderWindow() {
    if (this.sliderFloat("Spring Force", layouter.getSpringForce(), 0.001f, 5.0f, "%.3f")) {
      layouter.setSpringForce(flContainer[0]);
    }
    if (this.sliderFloat("Spring Force Roots", layouter.getSpringForceRoot(), 0.001f, 5.0f, "%.3f")) {
      layouter.setSpringForceRoot(flContainer[0]);
    }
    if (this.sliderInt("Repulsion Force", layouter.getRepulsionForce(), 1, 1000)) {
      layouter.setRepulsionForce(iContainer[0]);
    }
    if (this.sliderInt("Ideal Spring Length", layouter.getIdealSpringLength(), 1, 5000)) {
      layouter.setIdealSpringLength(iContainer[0]);
    }
    if (this.sliderInt("Ideal Spring Length Roots", layouter.getIdealSpringLengthRoot(), 1, 5000)) {
      layouter.setIdealSpringLengthRoot(iContainer[0]);
    }
    if (this.sliderFloat("Damping Factor", layouter.getDampingFactor(), 0.01f, 1.0f, "%.2f")) {
      layouter.setDampingFactor(flContainer[0]);
    }
    if (this.sliderFloat("Stability Threshold", layouter.getThreshold(), 0.1f, 10.0f, "%.1f")) {
      layouter.setThreshold(flContainer[0]);
    }
    if (this.sliderInt("Max Velocity", layouter.getMaxVelocity(), 1, 100)) {
      layouter.setMaxVelocity(iContainer[0]);
    }
    if (this.sliderFloat("Gravity Force", layouter.getGravityForce(), 0.1f, 1.0f, "%.3f")) {
      layouter.setGravityForce(flContainer[0]);
    }

    ImGui.checkbox("Stable", layouter.isLayoutStable());
    ImGui.sameLine();
    if (ImGui.checkbox("Manual Root Positioning", layouter.getLayoutRootsManually())) {
      layouter.setLayoutRootsManually(!layouter.getLayoutRootsManually());
    }
  }

}
