package com.hagenberg.jarvis.graph.render.nodes;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.imgui.Vec2;
import com.hagenberg.jarvis.graph.render.attributes.Attribute;

import imgui.ImVec2;
import imgui.extension.imnodes.ImNodes;

public abstract class Node {
  protected final int nodeId;

  // positioning
  protected Vec2 position = new Vec2();
  protected Vec2 velocity = new Vec2();
  protected boolean frozen = false;
  protected int width = 0;

  protected final List<Attribute> attributes = new ArrayList<>();

  protected final List<Node> InNeighbors = new ArrayList<>();
  protected final List<Node> OutNeighbors = new ArrayList<>();

  public Node(int nodeId) {
    this.nodeId = nodeId;
  }

  public void render() {
    preNode();
    ImNodes.beginNode(nodeId);
    preHeader();
    beginHeader();
    headerContent();
    endHeader();
    content();
    ImNodes.endNode();
    postNode();
  }

  public Vec2 getGridPosition() {
    ImVec2 gridPos = new ImVec2();
    ImNodes.getNodeGridSpacePos(nodeId, gridPos);
    return new Vec2(gridPos.x, gridPos.y);
  }
  
  protected void preNode() {
  }

  protected void preHeader() {
    ImNodes.setNodeDraggable(nodeId, true);
    width = (int) ImNodes.getNodeDimensionsX(nodeId);
    ImNodes.setNodeGridSpacePos(nodeId, position.x, position.y);
  }

  protected void beginHeader() {
    ImNodes.beginNodeTitleBar();
  }

  protected abstract void headerContent();

  protected void endHeader() {
    ImNodes.endNodeTitleBar();
  }

  protected abstract void content();

  protected void postNode() {
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

  public int getWidth() {
    return width;
  }

  public List<Attribute> getAttributes() {
    return attributes;
  }

  public void addAttribute(Attribute attribute) {
    attributes.add(attribute);
  }

  public List<Node> getInNeighbors() {
    return InNeighbors;
  }

  public List<Node> getOutNeighbors() {
    return OutNeighbors;
  }

  public void clearNeighbors() {
    InNeighbors.clear();
    OutNeighbors.clear();
  }
}