package com.hagenberg.jarvis.graph;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.imgui.Vec2;

public class LayoutNode {
  public final long id;
  public Vec2 position;
  public Vec2 velocity = new Vec2(0, 0);
  public List<LayoutNode> neighbors = new ArrayList<>();
  public boolean frozen = false;

  public LayoutNode(long id) {
    this(0, 0, id);
  }

  public LayoutNode(int x, int y, long id) {
    this.id = id;
    this.position = new Vec2(x, y);
  }
}