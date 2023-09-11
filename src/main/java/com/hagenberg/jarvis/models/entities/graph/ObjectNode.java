package com.hagenberg.jarvis.models.entities.graph;

import java.util.ArrayList;
import java.util.List;

public class ObjectNode extends Node {
    private long id; // ID from JDI
    private final List<MemberVariable> member = new ArrayList<>();

    @Override
    public void loadContents() {

    }
}
