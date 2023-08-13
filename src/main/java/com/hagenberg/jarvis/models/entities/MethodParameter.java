package com.hagenberg.jarvis.models.entities;

public class MethodParameter {
private String type;
    private String name;
    private String value;

    public MethodParameter(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getSimpleType() {
        String[] parts = type.split("\\.");
        return parts[parts.length - 1];
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
