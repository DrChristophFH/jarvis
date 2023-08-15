package com.hagenberg.jarvis.models;

import com.hagenberg.jarvis.models.entities.GraphObject;
import com.sun.jdi.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ObjectGraphModel {
    // Settings for the object graph
    private final IntegerProperty discoveryDepth = new SimpleIntegerProperty(4);
    private final ObservableList<String> fullyDiscoveryClasses = FXCollections.observableArrayList();
    private final ObservableList<String> stopDiscoveryClasses = FXCollections.observableArrayList();

    // The root objects of the object graph
    private final ObservableList<GraphObject> rootObjects = FXCollections.observableArrayList();

    public int getDiscoveryDepth() {
        return discoveryDepth.get();
    }

    public IntegerProperty discoveryDepthProperty() {
        return discoveryDepth;
    }

    public ObservableList<String> getFullyDiscoveryClasses() {
        return fullyDiscoveryClasses;
    }

    public ObservableList<String> getStopDiscoveryClasses() {
        return stopDiscoveryClasses;
    }

    public ObservableList<GraphObject> getRootObjects() {
        return rootObjects;
    }

    public void addRootObject(GraphObject rootObject) {
        rootObjects.add(rootObject);
    }
}
