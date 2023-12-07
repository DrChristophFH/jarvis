package com.hagenberg.jarvis.models.entities.wrappers;

import com.sun.jdi.Type;

public abstract class JType implements Refreshable {
  private final Type jdiType;
  private String name;

  public JType(Type type) {
    this.jdiType = type;
  }

  public String name() {
    return name;
  }

  @Override
  public void refresh() {
    name = jdiType.name();
  }
}
