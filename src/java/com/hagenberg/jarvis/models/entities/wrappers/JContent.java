package com.hagenberg.jarvis.models.entities.wrappers;

public class JContent {
  private final int index;
  private JValue value;

  public JContent(int index, JValue value) {
    this.index = index;
    this.value = value;
  }

  public int index() {
    return index;
  }

  public String name() {
    return "[" + index + "]";
  }

  public JValue value() {
    return value;
  }

  public void setValue(JValue value) {
    this.value = value;
  }
}
