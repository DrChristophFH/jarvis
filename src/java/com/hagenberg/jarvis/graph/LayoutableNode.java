package com.hagenberg.jarvis.graph;

import java.util.List;

import com.hagenberg.imgui.Vec2;

public interface LayoutableNode {
  public Vec2 getPosition();
  public void setPosition(Vec2 position);

  public Vec2 getVelocity();
  public void setVelocity(Vec2 velocity);

  public boolean isFrozen();
  public void setFrozen(boolean frozen);

  public int getLength();
  public void setLength(int length);

  public boolean isLayouted();

  public int getNodeId();
  public void setNodeId(int nodeId);

  public List<LayoutableNode> getInNeighbors();
  public List<LayoutableNode> getOutNeighbors();
}