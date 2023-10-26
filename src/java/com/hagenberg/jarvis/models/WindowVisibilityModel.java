package com.hagenberg.jarvis.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;

import java.util.HashMap;
import java.util.Map;

public class WindowVisibilityModel {
    private final Map<Node, BooleanProperty> visibilities = new HashMap<>();

    public void addVisibilityState(Node window) {
        visibilities.put(window, new SimpleBooleanProperty(true));
    }

    public BooleanProperty getVisibilityState(Node window) {
        return visibilities.get(window);
    }
}
