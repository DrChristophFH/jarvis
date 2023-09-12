package com.hagenberg.jarvis.models.entities.graph;

import java.util.ArrayList;
import java.util.List;

public class ObjectGNode extends GNode {
    private long id; // ID from JDI
    private final List<MemberGVariable> member = new ArrayList<>();

    public ObjectGNode(long id, String type) {
        super(type);
        this.id = id;
    }

    public void addMember(MemberGVariable memberVariable) {
        member.add(memberVariable);
    }

    @Override
    public void loadContents() {

    }
}