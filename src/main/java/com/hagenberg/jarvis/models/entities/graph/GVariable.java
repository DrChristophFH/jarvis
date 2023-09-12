package com.hagenberg.jarvis.models.entities.graph;

public class GVariable {
    protected String name;
    protected GNode node; // This can be an instance of PrimitiveNode, ArrayNode, or ReferenceNode

    public GVariable(String name, GNode node) {
        this.name = name;
        this.node = node;
    }

    public String getName() {
        return name;
    }

    public GNode getNode() {
        return node;
    }

    public static GVariable fromNode(GNode node, String name) {
        return new GVariable(name, node);
    }
}
