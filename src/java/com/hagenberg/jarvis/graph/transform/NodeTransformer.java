package com.hagenberg.jarvis.graph.transform;

import com.hagenberg.jarvis.graph.render.nodes.Node;

public abstract class NodeTransformer<T> {
  protected String name;
  
  public String getName() {
    return name;
  }

  public abstract Node transform(T object, IdProvider idProvider, LinkRegisterCallback linkRegisterCallback);
}