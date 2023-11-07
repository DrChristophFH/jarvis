package com.hagenberg.jarvis.views;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.flag.ImNodesMiniMapLocation;
import imgui.flag.ImGuiCond;
import imgui.type.ImInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.hagenberg.imgui.Vec2;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.graph.GraphLayouter;
import com.hagenberg.jarvis.graph.LayoutableNode;
import com.hagenberg.jarvis.graph.OGMTransformer;
import com.hagenberg.jarvis.graph.rendering.Link;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

public class ObjectGraph extends View {

  private GraphLayouter layouter = new GraphLayouter();
  private ObjectGraphModel model = new ObjectGraphModel();
  private OGMTransformer transformer = new OGMTransformer(model);

  int[] position = new int[2];
  ImInt id = new ImInt();
  int[] edge = new int[2];

  public ObjectGraph() {
    setName("Object Graph");
    model.addObserver(layouter);
  }

  public ObjectGraphModel getObjectGraphModel() {
    return model;
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
    Set<LayoutableNode> nodes = transformer.getNodes();
    Set<LayoutableNode> roots = transformer.getRoots();

    layouter.layoutRunner(nodes, roots);

    ImNodes.beginNodeEditor();

    drawGraph();

    ImNodes.miniMap(0.2f, ImNodesMiniMapLocation.BottomLeft);
    ImNodes.endNodeEditor();

    // update positions from dragging
    if (ImNodes.numSelectedNodes() > 0) {
      ImVec2 pos = new ImVec2();
      for (LayoutableNode node : nodes) {
        ImNodes.getNodeGridSpacePos(node.getNodeId(), pos);
        node.setPosition(new Vec2(pos));
      }
      for (LayoutableNode node : roots) {
        ImNodes.getNodeGridSpacePos(node.getNodeId(), pos);
        node.setPosition(new Vec2(pos));
      }
      layouter.update(); // TODO only update if positions changed
    }

    transformer.recalcWidthsIfNecessary();
  }

  private void drawGraph() {
    List<Link> links = new ArrayList<>();

    for (ObjectGNode node : model.getObjects()) {
      RendererRegistry.getInstance().getObjectRenderer(node).render(node, links);
    }

    for (LocalGVariable localVar : model.getLocalVariables()) {
      RendererRegistry.getInstance().getLocalVariableRenderer(localVar).render(localVar, links);
    }

    int linkId = 0;
    for (Link link : links) {
      ImNodes.link(linkId++, link.startNodeId(), link.endNodeId());
    }
  }
}