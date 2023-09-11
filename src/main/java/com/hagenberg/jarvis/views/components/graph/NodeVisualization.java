package com.hagenberg.jarvis.views.components.graph;

import com.hagenberg.jarvis.models.entities.graph.Node;

public abstract class NodeVisualization {
    protected Node node;

    public NodeVisualization(Node node) {
        this.node = node;
    }

    // Method to render visualization (you'd define a method signature that fits with your JavaFX setup)
    public abstract void render();
}

