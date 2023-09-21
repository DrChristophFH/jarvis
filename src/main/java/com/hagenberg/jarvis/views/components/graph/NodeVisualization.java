package com.hagenberg.jarvis.views.components.graph;

import com.hagenberg.jarvis.models.entities.graph.GNode;

public abstract class NodeVisualization {
    protected GNode node;

    public NodeVisualization(GNode node) {
        this.node = node;
    }

    // Method to render visualization (you'd define a method signature that fits with your JavaFX setup)
    public abstract void render();
}

