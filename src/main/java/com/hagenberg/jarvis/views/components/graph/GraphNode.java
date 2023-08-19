package com.hagenberg.jarvis.views.components.graph;

import javafx.scene.layout.Pane;

public interface GraphNode {
    Pane getNodeVisual();
    void setPosition(double x, double y);
}
