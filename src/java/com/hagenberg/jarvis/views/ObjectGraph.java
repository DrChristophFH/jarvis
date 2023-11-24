package com.hagenberg.jarvis.views;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.flag.ImNodesColorStyle;
import imgui.extension.imnodes.flag.ImNodesMiniMapLocation;
import imgui.extension.imnodes.flag.ImNodesStyleVar;
import imgui.flag.ImGuiCond;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.graph.GraphLayouter;
import com.hagenberg.jarvis.graph.render.Link;
import com.hagenberg.jarvis.graph.render.RenderModel;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.graph.transform.GraphTransformer;
import com.hagenberg.jarvis.models.ObjectGraphModel;

public class ObjectGraph extends View {

  private final GraphLayouter layouter = new GraphLayouter();
  private final ObjectGraphModel objectGraph = new ObjectGraphModel();
  private final GraphTransformer graphTransformer = new GraphTransformer(objectGraph, this);

  private RenderModel stagedRenderGraph;
  private RenderModel renderGraph = new RenderModel();

  public ObjectGraph() {
    setName("Object Graph");
  }

  public ObjectGraphModel getObjectGraphModel() {
    return objectGraph;
  }

  public GraphTransformer getGraphTransformer() {
    return graphTransformer;
  }

  public RenderModel getRenderModel() {
    return renderGraph;
  }

  public void stageRenderModel(RenderModel rm) {
    this.stagedRenderGraph = rm;
  }

  public GraphLayouter getLayouter() {
    return layouter;
  }

  @Override
  public void render() {
    ImGui.setNextWindowSize(800, 800, ImGuiCond.FirstUseEver);
    super.render();
  }

  @Override
  protected void renderWindow() {
    ImNodes.beginNodeEditor();

    drawGraph();

    ImNodes.miniMap(0.2f, ImNodesMiniMapLocation.BottomLeft);
    ImNodes.endNodeEditor();

    layouter.layoutRunner(renderGraph); 
  }

  private void drawGraph() {
    // swap render graph if new one is available
    if (stagedRenderGraph != null) {
      renderGraph = stagedRenderGraph;
      layouter.setUpdated();
      stagedRenderGraph = null;
    }

    for (Node node : renderGraph.getRoots()) {
      node.render();
    }

    for (Node node : renderGraph.getChildren()) {
      node.render();
    }

    int linkId = 0;
    for (Link link : renderGraph.getLinks()) {
      boolean isSelected = ImNodes.isLinkSelected(linkId);
      if (isSelected) {
        ImNodes.pushStyleVar(ImNodesStyleVar.LinkThickness, 5.0f);
        ImNodes.pushColorStyle(ImNodesColorStyle.LinkSelected, Colors.LinkSelected);
      }
      ImNodes.link(linkId++, link.startNodeId(), link.endNodeId());
      if (isSelected) {
        ImNodes.popColorStyle();
        ImNodes.popStyleVar();
      }
    }
  }
}