package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.ArrayReference;

public class JArrayReference extends JObjectReference {
  private final List<JValue> contents = new ArrayList<>();

  public JArrayReference(ArrayReference jdiArrayReference, JType type) {
    super(jdiArrayReference, type);
  }

  public void addContent(JValue arrayMember) {
    contents.add(arrayMember);
  }

  @Override
  public List<JValue> getValues() {
    return contents;
  }
}
