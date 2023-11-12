package com.hagenberg.jarvis.models.entities;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.Field;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;

public abstract class JReferenceType {
  private final ReferenceType referenceType;

  private final List<Field> fields = new ArrayList<>();
  private final List<Method> methods = new ArrayList<>();
  private String name;

  public JReferenceType(ReferenceType referenceType) {
    this.referenceType = referenceType;
  }

  public ReferenceType getReferenceType() {
    return referenceType;
  }

  public List<Field> allFields() {
    return fields;
  }

  public List<Method> allMethods() {
    return methods;
  }

  public String name() {
    return name;
  }

  public boolean isAbstract() {
    return referenceType.isAbstract();
  }

  public boolean isFinal() {
    return referenceType.isFinal();
  }

  public boolean isPrepared() {
    return referenceType.isPrepared();
  }

  public boolean isStatic() {
    return referenceType.isStatic();
  }

  /**
   * Requeries all the information from the debuggee.
   */
  public void refresh() {
    fields.clear();
    fields.addAll(referenceType.allFields());
    methods.clear();
    methods.addAll(referenceType.allMethods());
    name = referenceType.name();
  }
}
