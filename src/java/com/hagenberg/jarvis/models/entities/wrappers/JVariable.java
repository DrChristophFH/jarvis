package com.hagenberg.jarvis.models.entities.wrappers;

public class JVariable implements ReferenceHolder {
  protected JValue value;
  protected final String name;

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
