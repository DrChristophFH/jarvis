package com.hagenberg.jarvis.views;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.flag.ImNodesColorStyle;
import imgui.extension.imnodes.flag.ImNodesMiniMapLocation;
import imgui.extension.imnodes.flag.ImNodesStyleVar;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.graph.GraphLayouter;
import com.hagenberg.jarvis.graph.render.Link;
import com.hagenberg.jarvis.graph.render.RenderModel;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.graph.transform.GraphTransformer;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.util.Procedure;

public class ObjectGraph extends View {

  private final GraphLayouter layouter = new GraphLayouter();
  private final ObjectGraphModel objectGraph = new ObjectGraphModel();
  private final GraphTransformer graphTransformer = new GraphTransformer(objectGraph, this);

  private final List<Procedure> nodeActions = new ArrayList<>();

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
    handleNodeEditorContextMenu();
    drawGraph();
    ImNodes.miniMap(0.2f, ImNodesMiniMapLocation.BottomLeft);
    ImNodes.endNodeEditor();
    
    handleContextActions();
    
    layouter.layoutRunner(renderGraph); 
  }

  private void handleContextActions() {
    for (Procedure action : nodeActions) {
      action.run();
    }
    nodeActions.clear();
  }

  private void handleNodeEditorContextMenu() {
    if (ImNodes.isEditorHovered() && ImGui.isMouseReleased(ImGuiMouseButton.Right)) {
      ImGui.openPopup("GraphCtx");
    }

    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5); // NodeEditor somehow overrides this so we have to set it here

    if (ImGui.beginPopup("GraphCtx")) {
      if (ImGui.menuItem("Freeze Selected")) {
        nodeActions.add(() -> forSelectedNodes(node -> node.setFrozen(true)));
      }
      if (ImGui.menuItem("Unfreeze Selected")) {
        nodeActions.add(() -> forSelectedNodes(node -> node.setFrozen(false)));
      }
      ImGui.endPopup();
    }

    ImGui.popStyleVar();
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

  private void forSelectedNodes(Consumer<Node> consumer) {
    Snippets.forSelectedNodes(nodeId -> {
      Node node = renderGraph.getNode(nodeId);
      if (node == null) return; // imnodes reporting back a node that does not exist anymore?
      consumer.accept(node);
    });
  }
}