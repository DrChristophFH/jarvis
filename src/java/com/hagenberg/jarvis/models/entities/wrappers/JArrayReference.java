package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.List;

import com.sun.jdi.ArrayReference;

public class JArrayReference extends JObjectReference {
  private final JValue[] contents;

  public JArrayReference(ArrayReference jdiArrayReference, JType type) {
    super(jdiArrayReference, type);
    contents = new JValue[jdiArrayReference.length()];
  }

  public void setContent(int index, JValue arrayMember) {
    contents[index] = arrayMember;
  }

  public JValue getContent(int index) {
    return contents[index];
  }

  public ArrayReference getJdiArrayReference() {
    return (ArrayReference) getJdiObjectReference();
  }

  @Override
  public List<JValue> getValues() {
    return List.of(contents);
  }
}
