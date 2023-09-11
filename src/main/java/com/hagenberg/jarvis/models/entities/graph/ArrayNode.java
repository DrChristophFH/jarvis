package com.hagenberg.jarvis.models.entities.graph;

import java.util.List;

public class ArrayNode extends ObjectNode {
    private List<Node> contents; // This can hold a list of PrimitiveNode, ArrayNode or ReferenceNode objects

    @Override
    public void loadContents() {
        // Implementation for loading array contents
    }

    // Getters, Setters, and other methods
}
