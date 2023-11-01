package com.hagenberg.jarvis.models.entities.graph;

import java.util.ArrayList;
import java.util.List;

public class ArrayGNode extends ObjectGNode {
  private final List<ContentGVariable> contents = new ArrayList<>();

  public ArrayGNode(long id, String type) {
    super(id, type);
  }

  public void addContent(ContentGVariable arrayMember) {
    contents.add(arrayMember);
  }

  public List<ContentGVariable> getContentGVariables() {
    return contents;
  }
}
