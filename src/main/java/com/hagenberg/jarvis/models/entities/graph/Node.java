package com.hagenberg.jarvis.models.entities.graph;

public abstract class Node {
    protected String type;
    protected boolean isLoaded = false; // to indicate if the node's contents are loaded

    public abstract void loadContents(); // For lazy loading

    public boolean isLoaded() {
        return isLoaded;
    }
}
