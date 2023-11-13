package com.hagenberg.jarvis.models.entities.graph;

import com.hagenberg.jarvis.models.entities.AccessModifier;
import com.sun.jdi.Field;
import com.sun.jdi.Type;

public class MemberGVariable extends GVariable {
  private final Field field; // the field this variable represents (for render mapping)
  private final ObjectGNode containingObject;
  // from JVM specification see
  // https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.5
  private final AccessModifier accessModifier; 

  public MemberGVariable(Field field, String name, Type staticType, ObjectGNode parentObj, int accessModifier) {
    super(name, staticType);
    this.field = field;
    this.containingObject = parentObj;
    this.accessModifier = new AccessModifier(accessModifier);
  }

  public Field getField() {
    return field;
  }

  public int getAccessModifierNumber() {
    return accessModifier.getNumber();
  }

  public AccessModifier getAccessModifier() {
    return accessModifier;
  }

  public ObjectGNode getContainingObject() {
    return containingObject;
  }
}
