package com.hagenberg.jarvis.models.entities.graph;

public abstract class GVariable {
    protected String name;
    protected GNode node; // This can be an instance of PrimitiveNode, ArrayNode, or ReferenceNode

    public GVariable(String name, GNode node) {
        this.name = name;
        this.node = node;
    }
}
