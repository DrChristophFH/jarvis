package com.hagenberg.jarvis.graph;

public class Edge {
  public Node source;
  public Node target;

  public Edge(Node source, Node target) {
    this.source = source;
    this.target = target;
  }
}