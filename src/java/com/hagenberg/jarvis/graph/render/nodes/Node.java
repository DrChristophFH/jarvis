package com.hagenberg.jarvis.graph.render.nodes;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.imgui.Vec2;
import com.hagenberg.jarvis.graph.render.attributes.Attribute;

public abstract class Node {
  protected final int nodeId;

  // positioning
  protected Vec2 position = new Vec2();
  protected Vec2 velocity = new Vec2();
  protected boolean frozen = false;
  protected int length = 0;

  protected final List<Attribute> attributes = new ArrayList<>();

  public Node(int nodeId) {
    this.nodeId = nodeId;
  }

  public abstract void render();

  @Override
  public int hashCode() {
    return nodeId; // is unique
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Node other = (Node) obj;
    if (nodeId != other.nodeId) return false;
    return true;
  }

  public int getNodeId() {
    return nodeId;
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

  public List<Attribute> getAttributes() {
    return attributes;
  }

  public void addAttribute(Attribute attribute) {
    attributes.add(attribute);
  }
}