package com.hagenberg.jarvis.models.entities;

import com.hagenberg.jarvis.util.TypeFormatter;

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
        return TypeFormatter.getSimpleType(type);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
