package com.hagenberg.jarvis.models.entities.graph;

public class MemberGVariable extends GVariable {
    private final int accessModifier; // from JVM specification see https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.5

    public MemberGVariable(String name, GNode node, int accessModifier) {
        super(name, node);
        this.accessModifier = accessModifier;
    }
}
