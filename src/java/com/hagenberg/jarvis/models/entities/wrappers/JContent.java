package com.hagenberg.jarvis.models.entities.wrappers;

public class JContent extends JVariable {
  private final int index;

  public JContent(int index, JValue value) {
    super(value, "[" + index + "]");
    this.index = index;
  }

  public int index() {
    return index;
  }
}
