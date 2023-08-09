package com.hagenberg.jarvis.models;

import javafx.beans.property.BooleanProperty;

import java.util.HashMap;
import java.util.Map;

public class WindowStateModel {

    Map<String, BooleanProperty> visibilities = new HashMap<>();

    public void addVisibilityState(String windowName, BooleanProperty visibility) {
        visibilities.put(windowName, visibility);
    }

    public BooleanProperty getVisibilityState(String windowName) {
        return visibilities.get(windowName);
    }
}
