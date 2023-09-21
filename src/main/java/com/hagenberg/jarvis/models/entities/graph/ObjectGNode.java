package com.hagenberg.jarvis.models.entities.graph;

import java.util.ArrayList;
import java.util.List;

public class ObjectGNode extends GNode {
    private final long id; // ID from JDI
    private final List<MemberGVariable> members = new ArrayList<>();

    public ObjectGNode(long id, String type) {
        super(type);
        this.id = id;
    }

    public void addMember(MemberGVariable memberVariable) {
        members.add(memberVariable);
    }

    public List<MemberGVariable> getMembers() {
        return members;
    }

    public long getId() {
        return id;
    }

    @Override
    public void loadContents() {

    }
}