package com.hagenberg.jarvis.models.entities.graph;

import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.Type;

/**
 * A graph node that represents a primitive value
 */
public class PrimitiveGNode extends GNode {
  private final PrimitiveValue primitiveValue; // Storing as Object to accommodate various primitive types

  public PrimitiveGNode(Type type, PrimitiveValue primitiveValue) {
    super(type);
    this.primitiveValue = primitiveValue;
  }

  public PrimitiveValue getPrimitiveValue() {
    return primitiveValue;
  }

  @Override
  public String getToString() {
    return primitiveValue.toString();
  }
}
