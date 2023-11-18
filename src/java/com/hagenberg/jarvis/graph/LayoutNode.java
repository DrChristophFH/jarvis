package com.hagenberg.jarvis.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.hagenberg.imgui.Vec2;

public class LayoutNode {
  private int nodeId = -1;
  private Vec2 position = new Vec2();
  private Vec2 velocity = new Vec2();
  private boolean frozen = false;
  private int length = 0;

  private final Set<LayoutNode> inNeighbors = new HashSet<>();
  private final Set<LayoutNode> outNeighbors = new HashSet<>();

  public LayoutNode(int nodeId) {
    this.nodeId = nodeId;
  }

  public int getNodeId() {
    return nodeId;
  }

  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  public Vec2 getPosition() {
    return position;
  }

  public void setPosition(Vec2 position) {
    this.position = position;
  }

  public Vec2 getVelocity() {
    return velocity;
  }

  public void setVelocity(Vec2 velocity) {
    this.velocity = velocity;
  }

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public Collection<LayoutNode> getInNeighbors() {
    return inNeighbors;
  }

  public void addInNeighbor(LayoutNode node) {
    inNeighbors.add(node);
  }

  public void clearInNeighbors() {
    inNeighbors.clear();
  }
  
  public Collection<LayoutNode> getOutNeighbors() {
    return outNeighbors;
  }

  public void addOutNeighbor(LayoutNode node) {
    outNeighbors.add(node);
  }

  public void clearOutNeighbors() {
    outNeighbors.clear();
  }

  public void link(LayoutNode node) {
    addOutNeighbor(node);
    node.addInNeighbor(this);
  }

  @Override
  public int hashCode() {
    return nodeId; // is unique
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    LayoutNode other = (LayoutNode) obj;
    if (nodeId != other.nodeId) return false;
    return true;
  }
}