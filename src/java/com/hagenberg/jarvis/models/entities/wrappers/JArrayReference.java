package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.jarvis.models.entities.graph.ContentGVariable;
import com.sun.jdi.ArrayReference;

public class JArrayReference extends JObjectReference {
  private final List<ContentGVariable> contents = new ArrayList<>();

  public JArrayReference(ArrayReference jdiArrayReference, JType type) {
    super(jdiArrayReference, type);
  }

  public void addContent(ContentGVariable arrayMember) {
    contents.add(arrayMember);
  }

  public List<ContentGVariable> getContent() {
    return contents;
  }
}
