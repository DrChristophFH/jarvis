package com.hagenberg.jarvis.graph;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.imgui.Vec2;

public class Node {
  public final int id;
  public Vec2 position;
  public Vec2 velocity = new Vec2(0, 0);
  public List<Node> neighbors = new ArrayList<>();
  public boolean frozen = false;

  public Node(int id) {
    this(0, 0, id);
  }

  public Node(int x, int y, int id) {
    this.id = id;
    this.position = new Vec2(x, y);
  }
}