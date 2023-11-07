package com.hagenberg.jarvis.graph;

import java.util.Random;

import com.hagenberg.imgui.Vec2;
import com.hagenberg.jarvis.util.Observer;

public class GraphLayouter implements Observer {
  private float springForce = 0.05f;
  private int idealSpringLength = 20;
  private float gravityForce = 0.05f;
  private int repulsionForce = 500;
  private int repulsionForceRoot = 500;
  private float dampingFactor = 0.65f;
  private float threshold = 0.1f;
  private int maxVelocity = 100;
  private Random random = new Random();

  private boolean isLayoutStable = false;

  public void update() {
    isLayoutStable = false;
  }

  public void layoutRunner(Iterable<LayoutableNode> nodes, Iterable<LayoutableNode> roots) {
    if (isLayoutStable()) return;

    isLayoutStable = true;

    System.out.println("Layouting");

    layoutRoots(roots);
    layoutNodes(nodes, roots);
  }

  private void layoutRoots(Iterable<LayoutableNode> roots) {
    // Root forces
    for (LayoutableNode root : roots) {
      Vec2 netForce = new Vec2(0, 0);
      for (LayoutableNode otherRoot : roots) {
        if (root == otherRoot) continue;

        double dy = otherRoot.getPosition().y - root.getPosition().y;

        // Repulsion force, only along y-axis
        double rf = repulsionForceRoot / (dy * dy);
        netForce.y -= rf * Math.signum(dy);
      }

      for (LayoutableNode neighbor : root.getNeighbors()) {
        double dy = neighbor.getPosition().y - root.getPosition().y;

        // Spring force, only along y-axis
        double sf = springForce * dy;
        netForce.y += sf;
      }

      if (Double.isNaN(netForce.y)) {
        netForce.y = random.nextFloat() * 10;
      }

      root.getVelocity().add(netForce).scale(dampingFactor).clampAbs(maxVelocity);

      if (isLayoutStable && Math.abs(root.getVelocity().y) > threshold) {
        isLayoutStable = false;
      }

      root.getPosition().add(root.getVelocity());
    }
  }

  private void layoutNodes(Iterable<LayoutableNode> nodes, Iterable<LayoutableNode> roots) {
    for (LayoutableNode node : nodes) {
      if (node.isFrozen()) continue;

      Vec2 netForce = new Vec2(0, 0);

      // Repulsion forces from roots
      for (LayoutableNode root : roots) {
        netForce.add(calcRepulsionForce(node, root));
      }

      // Repulsion forces from other nodes
      for (LayoutableNode other : nodes) {
        if (node == other) continue;

        netForce.add(calcRepulsionForce(node, other));
      }

      // Spring forces from neighbors
      for (LayoutableNode neighbor : node.getNeighbors()) {
        netForce.add(calcSpringForce(node, neighbor)).subtract(calcRepulsionForce(node, neighbor));
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

  private Vec2 calcSpringForce(LayoutableNode node, LayoutableNode neighbor) {
    Vec2 result = new Vec2(0, 0);
    double dx = neighbor.getPosition().x - node.getPosition().x;
    double dy = neighbor.getPosition().y - node.getPosition().y;
    double distance = Math.sqrt(dx * dx + dy * dy);

    // Spring force
    double sf = springForce * distance / idealSpringLength;

    result.x += sf * dx / distance;
    result.y += sf * dy / distance;

    return result;
  }

  private Vec2 calcRepulsionForce(LayoutableNode node, LayoutableNode localVariable) {
    Vec2 result = new Vec2(0, 0);
    double dx = localVariable.getPosition().x - node.getPosition().x;
    double dy = localVariable.getPosition().y - node.getPosition().y;
    double distance = Math.sqrt(dx * dx + dy * dy);

    // Repulsion force
    double rf = repulsionForce / distance / distance;

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

  public int getRepulsionForceRoot() {
    return repulsionForceRoot;
  }

  public void setRepulsionForceRoot(int repulsionForceRoot) {
    this.repulsionForceRoot = repulsionForceRoot;
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
}
