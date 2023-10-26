package com.hagenberg.jarvis.models.entities.graph;

import java.util.ArrayList;
import java.util.List;

public class ArrayGNode extends ObjectGNode {
    private final List<GNode> contents = new ArrayList<>();

    public ArrayGNode(long id, String type) {
        super(id, type);
    }

    public void addContent(GNode node) {
        contents.add(node);
    }

    public List<GNode> getContents() {
        return contents;
    }

    @Override
    public void loadContents() {
        // Implementation for loading array contents
    }

    // Getters, Setters, and other methods
}
