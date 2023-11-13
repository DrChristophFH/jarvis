package com.hagenberg.jarvis.models.entities.graph;

import java.util.Objects;

import com.sun.jdi.Type;

public class GVariable {
  private String name;
  private Type staticType;
  private GNode node;

  public GVariable(String name, Type staticType) {
    this.name = name;
    this.staticType = staticType;
  }

  public GVariable(String name, Type staticType, GNode node) {
    this(name, staticType);
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

  public Type getStaticType() {
    return staticType;
  }

  public String getStaticTypeName () {
    return staticType.name();
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
