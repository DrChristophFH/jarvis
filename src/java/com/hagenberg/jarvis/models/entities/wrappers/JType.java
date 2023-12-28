package com.hagenberg.jarvis.models.entities.wrappers;

import com.sun.jdi.Type;

public abstract class JType {
  private final Type jdiType;

  private final String name;

  public JType(Type type) {
    this.jdiType = type;
    this.name = type.name();
  }

  public Type getJdiType() {
    return jdiType;
  }

  public String name() {
    return name;
  }

  public String getSimpleName() {
    return name.substring(name.lastIndexOf(".") + 1);
  }
}
