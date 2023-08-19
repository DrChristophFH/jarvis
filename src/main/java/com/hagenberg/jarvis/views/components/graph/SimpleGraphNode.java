package com.hagenberg.jarvis.views.components.graph;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class SimpleGraphNode implements GraphNode {

    private final Pane visual;
    private final Circle circle;
    private final Text label;

    public SimpleGraphNode(String labelContent, double x, double y) {
        circle = new Circle(30, Color.LIGHTBLUE); // 30 is the radius
        label = new Text(labelContent);

        // Center the label in the circle
        label.setLayoutX(-label.getLayoutBounds().getWidth() / 2);
        label.setLayoutY(label.getLayoutBounds().getHeight() / 4);

        visual = new Pane(circle, label);
        visual.relocate(x, y);
    }

    @Override
    public Pane getNodeVisual() {
        return visual;
    }

    @Override
    public void setPosition(double x, double y) {
        visual.relocate(x, y);
    }
}
