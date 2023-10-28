package com.hagenberg.jarvis.models.entities.graph;

import java.util.ArrayList;
import java.util.List;

public class ArrayGNode extends ObjectGNode {
  private final List<MemberGVariable> contents = new ArrayList<>();

  public ArrayGNode(long id, String type) {
    super(id, type);
  }

  @Override
  public void loadContents() {
    // Implementation for loading array contents
  }

  public void addContent(MemberGVariable arrayMember) {
    contents.add(arrayMember);
  }

  public List<MemberGVariable> getContents() {
    return contents;
  }
}
