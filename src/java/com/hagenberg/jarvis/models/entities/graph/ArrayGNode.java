package com.hagenberg.jarvis.models.entities.graph;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.jarvis.graph.LayoutableNode;
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

  @Override
  public List<LayoutableNode> getOutNeighbors() {
    List<LayoutableNode> neighbors = super.getOutNeighbors();

    for (ContentGVariable content : contents) {
      if (content.getNode() instanceof ObjectGNode obj) {
        neighbors.add(obj);
      }
    }

    return neighbors;
  }
}
