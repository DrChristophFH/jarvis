package com.hagenberg.jarvis.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hagenberg.imgui.Vec2;

public class GraphLayouter {
  private double springForce = 0.05;
  private double repulsionForce = 500;
  private double dampingFactor = 0.95;

  private Map<Integer, Node> nodes = new HashMap<>();
  private List<Edge> edges = new ArrayList<>();
  private double threshold = 0.1;
  private boolean isLayoutStable = false;

  public void addNode(int x, int y, int id) {
    Node node = new Node(x, y, id);
    nodes.put(node.id, node);
  }

  public void addEdge(int source, int target) {
    Node sourceNode = nodes.get(source);
    Node targetNode = nodes.get(target);
    edges.add(new Edge(sourceNode, targetNode));
    sourceNode.neighbors.add(targetNode);
    targetNode.neighbors.add(sourceNode);
    isLayoutStable = false;
  }

  public void layoutRunner() {
    if (isLayoutStable()) return;

    isLayoutStable = true;

    for (Node node : nodes.values()) {
      if (node.frozen) continue;

      Vec2 netForce = new Vec2(0, 0);
      for (Node other : nodes.values()) {
        if (node == other) continue;

        double dx = other.position.x - node.position.x;
        double dy = other.position.y - node.position.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Repulsion force
        double rf = this.repulsionForce / distance / distance;
        netForce.x -= rf * dx / distance;
        netForce.y -= rf * dy / distance;
      }

      for (Node neighbor : node.neighbors) {
        double dx = neighbor.position.x - node.position.x;
        double dy = neighbor.position.y - node.position.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Spring force
        double sf = this.springForce * distance;
        netForce.x += sf * dx / distance;
        netForce.y += sf * dy / distance;
      }

      System.out.println((dampingFactor * (node.velocity.x + netForce.x)));
      System.out.println((dampingFactor * (node.velocity.y + netForce.y)));

      node.velocity.x = (int) (dampingFactor * (node.velocity.x + netForce.x));
      node.velocity.y = (int) (dampingFactor * (node.velocity.y + netForce.y));

      if (isLayoutStable && (Math.abs(node.velocity.x) > threshold || Math.abs(node.velocity.y) > threshold)) {
        isLayoutStable = false;
      }

      node.position.x += node.velocity.x;
      node.position.y += node.velocity.y;
    }
  }

  public boolean isLayoutStable() {
    return isLayoutStable;
  }

  public Iterable<Node> getNodes() {
    return nodes.values();
  }

  public Iterable<Edge> getEdges() {
    return edges;
  }

  public double getSpringForce() {
    return springForce;
  }

  public void setSpringForce(double springForce) {
    this.springForce = springForce;
    isLayoutStable = false;
  }

  public double getRepulsionForce() {
    return repulsionForce;
  }

  public void setRepulsionForce(double repulsionForce) {
    this.repulsionForce = repulsionForce;
    isLayoutStable = false;
  }

  public double getDampingFactor() {
    return dampingFactor;
  }

  public void setDampingFactor(double dampingFactor) {
    this.dampingFactor = dampingFactor;
    isLayoutStable = false;
  }

  public void setThreshold(double threshold) {
    this.threshold = threshold;
    isLayoutStable = false;
  }

  public double getThreshold() {
    return threshold;
  }
}
