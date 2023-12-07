package com.hagenberg.jarvis.models.entities.wrappers;

public abstract class JValue {
  protected JType type;

  public JValue(JType type) {
    this.type = type;
  }

  public JType type() {
    return type;
  }

  public String getTypeName () {
    return type.name();
  }

  public abstract String getToString();
}
