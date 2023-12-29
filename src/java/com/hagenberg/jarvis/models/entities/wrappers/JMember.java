package com.hagenberg.jarvis.models.entities.wrappers;

public class JMember extends JVariable {
  private final JField field;
  
  public JMember(JField field, JValue value) {
    super(value, field.name());
    this.field = field;
  }

  public JField field() {
    return field;
  }
}
