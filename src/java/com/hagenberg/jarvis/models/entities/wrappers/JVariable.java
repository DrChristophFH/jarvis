package com.hagenberg.jarvis.models.entities.wrappers;

public abstract class JVariable {
  protected final String name;
  protected JValue value;

  public JVariable(JValue value, String name) {
    this.value = value;
    this.name = name;
  }

  public JValue value() {
    return value;
  }

  public void setValue(JValue value) {
    this.value = value;
  }

  public String name() {
    return name;
  }
}
