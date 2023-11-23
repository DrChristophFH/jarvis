package com.hagenberg.jarvis.graph.render;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.jarvis.graph.render.nodes.Node;

public class RenderModel {
  private final List<Node> nodes = new ArrayList<>();
  private final List<Link> links = new ArrayList<>();

  public void addNode(Node node) {
    nodes.add(node);
  }

  public void addLink(int sourceId, int targetId) {
    links.add(new Link(sourceId, targetId));
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public List<Link> getLinks() {
    return links;
  }

  public void clearNodes() {
    this.nodes.clear();
  }

  public void clearLinks() {
    this.links.clear();

    for (Node node : nodes) {
      node.clearNeighbors();
    }
  }
}
