package com.hagenberg.jarvis.models.entities.graph;

public abstract class Variable {
    protected String name;
    protected Node node; // This can be an instance of PrimitiveNode, ArrayNode, or ReferenceNode
}
