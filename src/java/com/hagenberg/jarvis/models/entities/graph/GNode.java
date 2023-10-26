package com.hagenberg.jarvis.models.entities.graph;

public abstract class GNode {
    protected String type;
    protected boolean isLoaded = false; // to indicate if the node's contents are loaded

    public GNode(String type) {
        this.type = type;
    }

    public abstract void loadContents(); // For lazy loading

    public boolean isLoaded() {
        return isLoaded;
    }

    public String getType() {
        return type;
    }
}
