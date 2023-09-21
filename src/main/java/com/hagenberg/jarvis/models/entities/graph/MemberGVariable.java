package com.hagenberg.jarvis.models.entities.graph;

import com.hagenberg.jarvis.models.entities.AccessModifier;

public class MemberGVariable extends GVariable {
    private final AccessModifier accessModifier; // from JVM specification see https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.5

    public MemberGVariable(String name, GNode node, int accessModifier) {
        super(name, node);
        this.accessModifier = new AccessModifier(accessModifier);
    }

    public int getAccessModifierNumber() {
        return accessModifier.getNumber();
    }

    public AccessModifier getAccessModifier() {
        return accessModifier;
    }
}
