package com.hagenberg.jarvis.models.entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GraphObject {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty value = new SimpleStringProperty();
    private boolean isPrimitive;
    private int accessModifier;
    private final ObservableList<GraphObject> members = FXCollections.observableArrayList();

    public GraphObject() {
        this("", "", "", false);
    }

    public GraphObject(String name, String type, String value, boolean isPrimitive) {
        this.name.set(name);
        this.type.set(type);
        this.value.set(value);
        this.isPrimitive = isPrimitive;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public ObservableList<GraphObject> getMembers() {
        return members;
    }

    public void setAccessModifierFromJVM(int accessModifier) {
        this.accessModifier = accessModifier;
    }

    public int getAccessModifier() {
        return accessModifier;
    }

    public void isPrimitive(boolean isPrimitive) {
        this.isPrimitive = isPrimitive;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }
}
