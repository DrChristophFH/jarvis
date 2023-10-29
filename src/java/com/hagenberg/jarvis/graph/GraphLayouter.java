package com.hagenberg.jarvis.graph;

import java.util.Random;

import com.hagenberg.imgui.Vec2;
import com.hagenberg.jarvis.util.Observer;

public class GraphLayouter implements Observer {
  private float springForce = 0.05f;
  private int repulsionForce = 500;
  private float dampingFactor = 0.65f;
  private float threshold = 0.1f;
  private int maxVelocity = 100;
  private Random random = new Random();

  private boolean isLayoutStable = false;

  public void update() {
    isLayoutStable = false;
  }

  public void layoutRunner(Iterable<LayoutableNode> nodes) {
    if (isLayoutStable()) return;

    isLayoutStable = true;

    System.out.println("Layouting");

    for (LayoutableNode node : nodes) {
      if (node.isFrozen()) continue;

      System.out.println("Node:" + node);
      System.out.println("\tPos:" + node.getPosition());

      Vec2 netForce = new Vec2(0, 0);
      for (LayoutableNode other : nodes) {
        //TODO use fluent Vec2 API
        if (node == other) continue;

        double dx = other.getPosition().x - node.getPosition().x;
        double dy = other.getPosition().y - node.getPosition().y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Repulsion force
        double rf = this.repulsionForce / distance / distance;

        netForce.x -= rf * dx / distance;
        netForce.y -= rf * dy / distance;
      }

      for (LayoutableNode neighbor : node.getNeighbors()) {
        double dx = neighbor.getPosition().x - node.getPosition().x;
        double dy = neighbor.getPosition().y - node.getPosition().y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Spring force
        double sf = this.springForce * distance;
        netForce.x += sf * dx / distance;
        netForce.y += sf * dy / distance;
      }

      // if netForce is NaN, set it to a random value to untangle the graph
      if (Double.isNaN(netForce.x) || Double.isNaN(netForce.y)) {
        netForce.x = random.nextFloat() * 10;
        netForce.y = random.nextFloat() * 10;
      }

      node.getVelocity().add(netForce).scale(dampingFactor).clampAbs(maxVelocity);

      System.out.println("\tVel:" + node.getVelocity());

      if (isLayoutStable && (Math.abs(node.getVelocity().x) > threshold || Math.abs(node.getVelocity().y) > threshold)) {
        isLayoutStable = false;
      }

      node.getPosition().add(node.getVelocity());
    }

    System.out.println("Layouting done");
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
