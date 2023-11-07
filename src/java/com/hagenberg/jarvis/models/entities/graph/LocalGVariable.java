package com.hagenberg.jarvis.models.entities.graph;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.imgui.Vec2;
import com.hagenberg.jarvis.graph.LayoutableNode;
import com.sun.jdi.Type;

public class LocalGVariable extends GVariable implements LayoutableNode {
  private final StackFrameInformation stackFrameInformation; // A class to store stack frame information
  private int nodeId; // ID for imnodes
  private Vec2 position = new Vec2(0, 0);
  private Vec2 velocity = new Vec2(0, 0);
  private int length = 0;
  private boolean frozen = false;
  private boolean layouted = true;

  public LocalGVariable(String name, Type staticType, StackFrameInformation stackFrameInformation) {
    super(name, staticType);
    this.stackFrameInformation = stackFrameInformation;
  }

  public StackFrameInformation getStackFrameInformation() {
    return stackFrameInformation;
  }

   @Override
  public Vec2 getPosition() {
    return position;
  }

  @Override
  public void setPosition(Vec2 position) {
    this.position = position;
  }

  @Override
  public Vec2 getVelocity() {
    return velocity;
  }

  @Override
  public void setVelocity(Vec2 velocity) {
    this.velocity = velocity;
  }

  @Override
  public int getLength() {
    return length;
  }

  @Override
  public void setLength(int length) {
    this.length = length;
  }

  @Override
  public boolean isFrozen() {
    return frozen;
  }

  @Override
  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
  }

  @Override
  public boolean isLayouted() {
    return layouted;
  }

  @Override
  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  @Override
  public int getNodeId() {
    return nodeId;
  }

  @Override
  public Iterable<LayoutableNode> getOutNeighbors() {
    List<LayoutableNode> neighbors = new ArrayList<>();

    if (getNode() instanceof ObjectGNode objectGNode) {
      neighbors.add(objectGNode);
    }

    return neighbors;
  }

  @Override
  public Iterable<LayoutableNode> getInNeighbors() {
    return new ArrayList<>();
  }
}