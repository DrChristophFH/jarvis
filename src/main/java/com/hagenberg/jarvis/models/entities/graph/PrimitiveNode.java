package com.hagenberg.jarvis.models.entities.graph;

/**
 * A graph node that represents a primitive value
 */
public class PrimitiveNode extends Node {
    private Object primitiveValue; // Storing as Object to accommodate various primitive types

    public PrimitiveNode(Object primitiveValue) {
        this.primitiveValue = primitiveValue;
        this.type = primitiveValue.getClass().getName();
    }

    @Override
    public void loadContents() {
        // Implementation for loading primitive value
    }
}
