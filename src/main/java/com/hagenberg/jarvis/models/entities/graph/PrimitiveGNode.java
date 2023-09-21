package com.hagenberg.jarvis.models.entities.graph;

/**
 * A graph node that represents a primitive value
 */
public class PrimitiveGNode extends GNode {
    private final Object primitiveValue; // Storing as Object to accommodate various primitive types

    public PrimitiveGNode(String type, Object primitiveValue) {
        super(type);
        this.primitiveValue = primitiveValue;
    }

    public Object getPrimitiveValue() {
        return primitiveValue;
    }

    @Override
    public void loadContents() {
        // Implementation for loading primitive value
    }
}
