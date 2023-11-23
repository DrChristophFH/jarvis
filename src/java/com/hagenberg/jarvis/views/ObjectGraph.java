package com.hagenberg.jarvis.views;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.flag.ImNodesMiniMapLocation;
import imgui.flag.ImGuiCond;

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
  private final RenderModel renderGraph = new RenderModel();
  private final GraphTransformer graphTransformer = new GraphTransformer(objectGraph, renderGraph);

  public ObjectGraph() {
    setName("Object Graph");
    objectGraph.addObserver(layouter);
  }

  public ObjectGraphModel getObjectGraphModel() {
    return objectGraph;
  }

  public GraphTransformer getGraphTransformer() {
    return graphTransformer;
  }

  public GraphLayouter getLayouter() {
    return layouter;
  }

  @Override
  public void render() {
    ImGui.setNextWindowSize(800, 800, ImGuiCond.FirstUseEver);
    objectGraph.lockModel();
    try {
      super.render();
    } finally {
      objectGraph.unlockModel();
    }
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
    for (Node node : renderGraph.getRoots()) {
      node.render();
    }

    for (Node node : renderGraph.getNodes()) {
      node.render();
    }

    int linkId = 0;
    for (Link link : renderGraph.getLinks()) {
      ImNodes.link(linkId++, link.startNodeId(), link.endNodeId());
    }
  }
}