package com.hagenberg.jarvis.graph.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hagenberg.jarvis.graph.render.nodes.Node;

public class RenderModel {
  private final Map<Integer, Node> nodes = new HashMap<>();
  private final List<Node> roots = new ArrayList<>();
  private final List<Node> children = new ArrayList<>();
  private final List<Link> links = new ArrayList<>();

  public void addChild(Node node) {
    children.add(node);
    addNode(node);
  }

  public void addRoot(Node node) {
    roots.add(node);
    addNode(node);
  }

  private void addNode(Node node) {
    nodes.put(node.getNodeId(), node);
  }

  public void addLink(int sourceId, int targetId) {
    links.add(new Link(sourceId, targetId));
  }

  public Node getNode(int nodeId) {
    return nodes.get(nodeId);
  }

  public List<Node> getChildren() {
    return children;
  }
  
  public List<Node> getRoots() {
    return roots;
  }

  public List<Link> getLinks() {
    return links;
  }

  // currently unused, needed if rm is not destroyed but just swapped and reused

  public void clearNodes() {
    this.nodes.clear();
    this.roots.clear();
    this.children.clear();
  }

  public void clearLinks() {
    this.links.clear();

    for (Node node : nodes.values()) {
      node.clearNeighbors();
    }
  }
}
