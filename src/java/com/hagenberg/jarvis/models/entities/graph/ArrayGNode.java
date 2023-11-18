package com.hagenberg.jarvis.models.entities.graph;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.Type;

public class ArrayGNode extends ObjectGNode {
  private final List<ContentGVariable> contents = new ArrayList<>();

  public ArrayGNode(long id, Type type) {
    super(id, type);
  }

  public void addContent(ContentGVariable arrayMember) {
    contents.add(arrayMember);
  }

  public List<ContentGVariable> getContentGVariables() {
    return contents;
  }
}
