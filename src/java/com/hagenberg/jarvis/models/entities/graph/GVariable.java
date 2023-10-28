package com.hagenberg.jarvis.models.entities.graph;

import java.util.Objects;

public class GVariable {
  protected String name;
  protected GNode node; // This can be an instance of PrimitiveNode, ArrayNode, or ReferenceNode

  public GVariable(String name) {
    this.name = name;
  }

  public GVariable(String name, GNode node) {
    this(name);
    this.node = node;
  }

  public String getName() {
    return name;
  }

  public GNode getNode() {
    return node;
  }

  public void setNode(GNode node) {
    this.node = node;
  }

  public static GVariable fromNode(GNode node, String name) {
    return new GVariable(name, node);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GVariable gVariable = (GVariable) o;
    return Objects.equals(name, gVariable.name) && Objects.equals(node, gVariable.node);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, node);
  }
}
