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

  public String getSimpleName() {
    return name.substring(name.lastIndexOf(".") + 1);
  }

  @Override
  public void refresh() {
    name = jdiType.name();
  }
}
