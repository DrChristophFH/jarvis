package com.hagenberg.jarvis.views;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.flag.ImNodesMiniMapLocation;
import imgui.flag.ImGuiCond;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.graph.GraphLayouter;
import com.hagenberg.jarvis.graph.LayoutNode;
import com.hagenberg.jarvis.graph.NodeEnumerator;
import com.hagenberg.jarvis.graph.rendering.Link;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

public class ObjectGraph extends View {

  private final List<Link> links = new ArrayList<>();
  private final Set<ObjectGNode> renderedObjects = new HashSet<>();
  private final Stack<ObjectGNode> objectsToRender = new Stack<>();

  private final List<LayoutNode> nodesToLayout = new ArrayList<>();
  private final List<LayoutNode> rootsToLayout = new ArrayList<>();

  private final GraphLayouter layouter = new GraphLayouter();
  private final ObjectGraphModel model = new ObjectGraphModel();
  private final NodeEnumerator nodeEnumerator = new NodeEnumerator(model);
  private final RendererRegistry rendererRegistry = new RendererRegistry();

  public ObjectGraph() {
    setName("Object Graph");
    model.addObserver(layouter);
  }

  public ObjectGraphModel getObjectGraphModel() {
    return model;
  }

  public NodeEnumerator getNodeEnumerator() {
    return nodeEnumerator;
  }

  public RendererRegistry getRendererRegistry() {
    return rendererRegistry;
  }

  public GraphLayouter getLayouter() {
    return layouter;
  }

  @Override
  public void render() {
    ImGui.setNextWindowSize(800, 800, ImGuiCond.FirstUseEver);
    model.lockModel();
    try {
      super.render();
    } finally {
      model.unlockModel();
    }
  }

  /**
   * Renderers use this method to add a link to the graph and the target node to the
   * rendering queue if it has not been rendered yet. Renderers therefore decide the layout.
   * @param startNodeId
   * @param target
   * @return
   */
  public Link addLink(int startNodeId, ObjectGNode target) {
    if (!renderedObjects.contains(target)) {
      objectsToRender.push(target);
      renderedObjects.add(target); // prevent adding the same object multiple times
    }
    Link link = new Link(startNodeId, target.getLayoutNode().getNodeId());
    links.add(link);
    return link;
  }

  public void registerNodeForLayout(LayoutNode node) {
    nodesToLayout.add(node);
  }

  public void registerRootForLayout(LayoutNode node) {
    rootsToLayout.add(node);
  }

  @Override
  protected void renderWindow() {
    nodesToLayout.clear();
    rootsToLayout.clear();
    renderedObjects.clear();
    links.clear();

    ImNodes.beginNodeEditor();

    drawGraph();

    ImNodes.miniMap(0.2f, ImNodesMiniMapLocation.BottomLeft);
    ImNodes.endNodeEditor();

    nodeEnumerator.reset(); // reset att id pool

    layouter.layoutRunner(nodesToLayout, rootsToLayout);
  }

  private void drawGraph() {
    // start from roots
    for (LocalGVariable localVar : model.getLocalVariables()) {
      rendererRegistry.getLocalRenderer(localVar).render(localVar, localVar.getLayoutNode().getNodeId(), this);
    }

    // process render queue
    while (!objectsToRender.isEmpty()) {
      ObjectGNode object = objectsToRender.pop();
      renderedObjects.add(object);
      rendererRegistry.getObjectRenderer(object).render(object, object.getLayoutNode().getNodeId(), this);
    }

    int linkId = 0;
    for (Link link : links) {
      ImNodes.link(linkId++, link.startNodeId(), link.endNodeId());
    }
  }
}