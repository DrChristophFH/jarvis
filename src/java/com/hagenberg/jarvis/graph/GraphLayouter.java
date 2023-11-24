package com.hagenberg.jarvis.graph;

import java.util.Random;

import com.hagenberg.imgui.Vec2;
import com.hagenberg.jarvis.graph.render.RenderModel;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.util.Observer;

import imgui.ImVec2;
import imgui.extension.imnodes.ImNodes;

public class GraphLayouter implements Observer {
  private float springForce = 4.5f;
  private float springForceRoot = 1.8f;
  private int idealSpringLength = 140;
  private int idealSpringLengthRoot = 230;
  private float gravityForce = 0.15f;
  private int repulsionForce = 345;
  private float dampingFactor = 0.95f;
  private float threshold = 0.1f;
  private int maxVelocity = 100;
  private Random random = new Random();

  private boolean isLayoutStable = false;
  private boolean layoutRootsManually = false;

  public void update() {
    isLayoutStable = false;
  }

  public void layoutRunner(RenderModel renderGraph) {

    updateNodeDragPositions(renderGraph);

    if (isLayoutStable) return;

    isLayoutStable = true; // assume stable until proven otherwise

    if (!layoutRootsManually) {
      layoutRoots(renderGraph.getRoots());
    }
    layoutNodes(renderGraph.getChildren(), renderGraph.getRoots());
  }

  /**
   * Update node positions from dragging
   * @param nodes normal nodes
   * @param roots root nodes
   */
  private void updateNodeDragPositions(RenderModel renderGraph) {
    int count = ImNodes.numSelectedNodes();
    if (count > 0) {
      int[] selectedNodes = new int[count];
      ImNodes.getSelectedNodes(selectedNodes);

      ImVec2 newPos = new ImVec2();
      Vec2 previousPos;

      for (int i = 0; i < count; i++) {
        ImNodes.getNodeGridSpacePos(selectedNodes[i], newPos);
        Node node = renderGraph.getNode(selectedNodes[i]);
        previousPos = node.getPosition();

        if (!previousPos.isEqualTo(newPos)) {
          previousPos.set(newPos); 
          isLayoutStable = false;
        }
      }
    }
  }

  private void layoutRoots(Iterable<Node> roots) {
    // Root forces
    int yOffset = 0;
    for (Node root : roots) {
      root.getPosition().y = yOffset;
      yOffset += idealSpringLengthRoot;
      root.getPosition().x = 0; // fixed x position
    }
  }

  private void layoutNodes(Iterable<Node> nodes, Iterable<Node> roots) {
    for (Node node : nodes) {
      if (node.isFrozen()) continue;

      Vec2 netForce = new Vec2(0, 0);

      // Repulsion forces from roots
      for (Node root : roots) {
        netForce.add(calcRepulsionForce(node, root));
      }

      // Repulsion forces from other nodes
      for (Node other : nodes) {
        if (node == other) continue;

        netForce.add(calcRepulsionForce(node, other));
      }

      // Spring forces from neighbors
      for (Node neighbor : node.getInNeighbors()) {
        netForce.add(calcSpringForce(node, neighbor, springForce, idealSpringLength + neighbor.getLength()));
      }

      for (Node neighbor : node.getOutNeighbors()) {
        netForce.add(calcSpringForce(node, neighbor, springForce, idealSpringLength + node.getLength()));
      }

      // Gravity force (to the right)
      netForce.x += gravityForce;

      // if netForce is NaN, set it to a random value to untangle the graph
      if (Double.isNaN(netForce.x) || Double.isNaN(netForce.y)) {
        netForce.x = random.nextFloat() * 10;
        netForce.y = random.nextFloat() * 10;
      }

      node.getVelocity().add(netForce).scale(dampingFactor).clampAbs(maxVelocity);

      if (isLayoutStable && (Math.abs(node.getVelocity().x) > threshold || Math.abs(node.getVelocity().y) > threshold)) {
        isLayoutStable = false;
      }

      node.getPosition().add(node.getVelocity());
    }
  }

  private Vec2 calcSpringForce(Node node, Node neighbor, float springForce, int idealSpringLength) {
    Vec2 result = new Vec2(0, 0);
    double dx = neighbor.getPosition().x - node.getPosition().x;
    double dy = neighbor.getPosition().y - node.getPosition().y;
    double distance = Math.sqrt(dx * dx + dy * dy);

    // Repulsion force (subtracted as springs already model repulsion for neighbours)
    double rf = repulsionForce / (distance * distance);
    // Spring force
    double sf = springForce * Math.log(distance / idealSpringLength);

    result.x += sf * dx / distance - rf * dx / distance;
    result.y += sf * dy / distance - rf * dy / distance;

    return result;
  }

  private Vec2 calcRepulsionForce(Node node, Node localVariable) {
    Vec2 result = new Vec2(0, 0);
    double dx = localVariable.getPosition().x - node.getPosition().x;
    double dy = localVariable.getPosition().y - node.getPosition().y;
    double distance = Math.sqrt(dx * dx + dy * dy);

    // Repulsion force
    double rf = repulsionForce / (distance * distance);

    result.x -= rf * dx / distance;
    result.y -= rf * dy / distance;

    return result;
  }

  public boolean isLayoutStable() {
    return isLayoutStable;
  }

  public float getSpringForce() {
    return springForce;
  }

  public void setSpringForce(float springForce) {
    this.springForce = springForce;
    isLayoutStable = false;
  }

  public float getSpringForceRoot() {
    return springForceRoot;
  }

  public void setSpringForceRoot(float springForceRoot) {
    this.springForceRoot = springForceRoot;
    isLayoutStable = false;
  }

  public int getRepulsionForce() {
    return repulsionForce;
  }

  public void setRepulsionForce(int repulsionForce) {
    this.repulsionForce = repulsionForce;
    isLayoutStable = false;
  }

  public float getGravityForce() {
    return gravityForce;
  }

  public void setGravityForce(float gravityForce) {
    this.gravityForce = gravityForce;
    isLayoutStable = false;
  }

  public int getIdealSpringLength() {
    return idealSpringLength;
  }

  public void setIdealSpringLength(int repulsionForceRoot) {
    this.idealSpringLength = repulsionForceRoot;
    isLayoutStable = false;
  }

  public int getIdealSpringLengthRoot() {
    return idealSpringLengthRoot;
  }

  public void setIdealSpringLengthRoot(int idealSpringLengthRoot) {
    this.idealSpringLengthRoot = idealSpringLengthRoot;
    isLayoutStable = false;
  }

  public float getDampingFactor() {
    return dampingFactor;
  }

  public void setDampingFactor(float dampingFactor) {
    this.dampingFactor = dampingFactor;
    isLayoutStable = false;
  }

  public void setThreshold(float threshold) {
    this.threshold = threshold;
    isLayoutStable = false;
  }

  public float getThreshold() {
    return threshold;
  }

  public int getMaxVelocity() {
    return maxVelocity;
  }

  public void setMaxVelocity(int maxVelocity) {
    this.maxVelocity = maxVelocity;
    isLayoutStable = false;
  }

  public void setLayoutRootsManually(boolean layoutRootsManually) {
    this.layoutRootsManually = layoutRootsManually;
  }

  public boolean getLayoutRootsManually() {
    return layoutRootsManually;
  }
}
