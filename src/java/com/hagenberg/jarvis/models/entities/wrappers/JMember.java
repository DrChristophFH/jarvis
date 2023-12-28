package com.hagenberg.jarvis.models.entities.wrappers;

public class JMember {
  private final JField field;
  private JValue value;
  
  public JMember(JField field, JValue value) {
    this.field = field;
    this.value = value;
  }

  public JField field() {
    return field;
  }

  public JValue value() {
    return value;
  }

  public void setValue(JValue value) {
    this.value = value;
  }
}
