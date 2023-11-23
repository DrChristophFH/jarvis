package com.hagenberg.jarvis.graph.transform;

import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.render.nodes.Node;

public abstract class AttributeTransformer<T> {
  protected String name;
  
  public String getName() {
    return name;
  }

  public abstract Attribute transform(T object, IdProvider idProvider, Node parent, LinkRegisterCallback linkRegisterCallback);
}
