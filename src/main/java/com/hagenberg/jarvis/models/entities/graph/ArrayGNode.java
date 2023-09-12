package com.hagenberg.jarvis.models.entities.graph;

import java.util.List;

public class ArrayGNode extends ObjectGNode {
    private List<GNode> contents; // This can hold a list of PrimitiveNode, ArrayNode or ReferenceNode objects

    public ArrayGNode(long id, String type) {
        super(id, type);
    }

    public void addContent(GNode node) {
        contents.add(node);
    }

    @Override
    public void loadContents() {
        // Implementation for loading array contents
    }

    // Getters, Setters, and other methods
}
