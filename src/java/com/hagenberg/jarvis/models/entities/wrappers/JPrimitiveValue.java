package com.hagenberg.jarvis.models.entities.wrappers;

import com.sun.jdi.PrimitiveValue;

/**
 * A graph node that represents a primitive value
 */
public class JPrimitiveValue extends JValue {
  private final PrimitiveValue jdiPrimitiveValue; // Storing as Object to accommodate various primitive types

  public JPrimitiveValue(PrimitiveValue jdiPrimitiveValue, JType type) {
    super(type);
    this.jdiPrimitiveValue = jdiPrimitiveValue;
  }

  public PrimitiveValue getJdiPrimitiveValue() {
    return jdiPrimitiveValue;
  }

  @Override
  public String getToString() {
    return jdiPrimitiveValue.toString();
  }

  @Override
  public void refresh() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'refresh'");
  }
}
