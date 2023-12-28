package com.hagenberg.jarvis.graph.transform;

import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.render.nodes.Node;

public abstract class AttributeTransformer<P, T> {
  protected String name;
  
  public String getName() {
    return name;
  }

  public abstract Attribute transform(P containingObject, T object, IdProvider idProvider, Node parent, LinkRegisterCallback linkRegisterCallback);
}
