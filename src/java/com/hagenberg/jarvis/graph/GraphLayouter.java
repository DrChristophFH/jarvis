package com.hagenberg.jarvis.graph;

import java.util.Random;

import com.hagenberg.imgui.Vec2;
import com.hagenberg.jarvis.util.Observer;

public class GraphLayouter implements Observer {
  private double springForce = 0.05;
  private double repulsionForce = 500;
  private double dampingFactor = 0.95;
  private double threshold = 0.1;
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

      System.out.println("\tPos:" + node.getPosition());
      System.out.println("\tNeighbours:" + node.getNeighbors());

      Vec2 netForce = new Vec2(0, 0);
      for (LayoutableNode other : nodes) {
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

      System.out.println("\t" + (dampingFactor * (node.getVelocity().x + netForce.x)));
      System.out.println("\t" + (dampingFactor * (node.getVelocity().y + netForce.y)));

      node.getVelocity().x = (int) (dampingFactor * (node.getVelocity().x + netForce.x));
      node.getVelocity().y = (int) (dampingFactor * (node.getVelocity().y + netForce.y));

      if (isLayoutStable
          && (Math.abs(node.getVelocity().x) > threshold || Math.abs(node.getVelocity().y) > threshold)) {
        isLayoutStable = false;
      }

      node.getPosition().x += node.getVelocity().x;
      node.getPosition().y += node.getVelocity().y;
    }

    System.out.println("Layouting done");
  }

  public boolean isLayoutStable() {
    return isLayoutStable;
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
