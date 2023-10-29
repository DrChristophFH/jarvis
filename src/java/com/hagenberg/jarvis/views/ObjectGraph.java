package com.hagenberg.jarvis.views;

import imgui.ImColor;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiButtonFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiMouseButton;
import imgui.type.ImInt;

import com.hagenberg.imgui.Draw;
import com.hagenberg.imgui.Vec2;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.graph.GraphLayouter;
import com.hagenberg.jarvis.graph.LayoutableNode;
import com.hagenberg.jarvis.graph.OGMTransformer;
import com.hagenberg.jarvis.models.ObjectGraphModel;

public class ObjectGraph extends View {

  private GraphLayouter layouter = new GraphLayouter();
  private ObjectGraphModel model = new ObjectGraphModel();
  private OGMTransformer transformer = new OGMTransformer(model);

  float[] springForce = new float[] { 0.05f };
  int[] repulsionForce = new int[] { 500 };
  float[] dampingFactor = new float[] { 0.95f };
  float[] threshold = new float[] { 0.1f };

  int[] position = new int[2];
  ImInt id = new ImInt();
  int[] edge = new int[2];

  private Vec2 scrolling = new Vec2(0, 0);
  private Vec2 origin = new Vec2(0, 0);

  private float MOUSE_THRESHOLD = 0.0f;

  public ObjectGraph() {
    setName("Object Graph");
    model.addObserver(layouter);
  }

  @Override
  public void render() {
    ImGui.setNextWindowSize(800, 800, ImGuiCond.FirstUseEver);
    super.render();
  }

  public ObjectGraphModel getObjectGraphModel() {
    return model;
  }

  @Override
  protected void renderWindow() {
    layouter.layoutRunner(transformer.getNodes());

    if (ImGui.sliderFloat("Spring Force", springForce, 0.01f, 1.0f, "%.2f")) {
      layouter.setSpringForce(springForce[0]);
    }
    if (ImGui.sliderInt("Repulsion Force", repulsionForce, 1, 10000)) {
      layouter.setRepulsionForce(repulsionForce[0] * 10);
    }
    if (ImGui.sliderFloat("Damping Factor", dampingFactor, 0.01f, 1.0f, "%.2f")) {
      layouter.setDampingFactor(dampingFactor[0]);
    }
    if (ImGui.sliderFloat("Stability Threshold", threshold, 0.01f, 1.0f, "%.2f")) {
      layouter.setThreshold(threshold[0]);
    }
    ImGui.checkbox("Stable", layouter.isLayoutStable());

    ImGui.separator();

    Vec2 canvasP0 = new Vec2(ImGui.getCursorScreenPos());
    Vec2 canvasSize = new Vec2(ImGui.getContentRegionAvail());
    Vec2 canvasP1 = new Vec2(canvasP0).add(canvasSize);

    handleCanvasInteraction(canvasP0, canvasSize);

    Draw draw = new Draw(ImGui.getWindowDrawList());
    draw.pushClipRect(canvasP0, canvasP1, true);
    draw.addRectFilled(canvasP0, canvasP1, ImColor.rgba(47, 49, 53, 255));

    drawGraph();

    draw.popClipRect();
  }

  private void drawGraph() {
    Draw offsetDraw = new Draw(ImGui.getWindowDrawList(), new Vec2(origin));

    // TODO: proper rendering logic based on whole OGM

    for (LayoutableNode node : transformer.getNodes()) {
      offsetDraw.addCircleFilled(node.getPosition(), 10, ImColor.rgb(255, 255, 255), 8);
    }

    // for (LayoutableEdge edge : transformer.getEdges()) {
    //   offsetDraw.addLine(edge.source.position, edge.target.position, ImColor.rgb(255, 255, 255), 2);
    // }
  }

  private void handleCanvasInteraction(Vec2 canvasP0, Vec2 canvasSize) {
    ImGuiIO io = ImGui.getIO();

    ImGui.invisibleButton("canvas", canvasSize.x, canvasSize.y, ImGuiButtonFlags.MouseButtonRight);

    // dragging right mouse button for moving
    if (ImGui.isItemActive() && ImGui.isMouseDragging(ImGuiMouseButton.Right, MOUSE_THRESHOLD)) {
      scrolling.add(io.getMouseDelta());
    }

    origin = new Vec2(canvasP0).add(scrolling);
  }
}