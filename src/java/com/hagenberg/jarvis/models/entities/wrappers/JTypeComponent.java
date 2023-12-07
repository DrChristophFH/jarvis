package com.hagenberg.jarvis.models.entities.wrappers;

import com.sun.jdi.TypeComponent;

public abstract class JTypeComponent implements Refreshable {
  private final TypeComponent typeComponent;
  private String name;
  private int modifiers;
  private String genericType;

  public JTypeComponent(TypeComponent typeComponent) {
    this.typeComponent = typeComponent;
  }

  public String name() {
    return name;
  }

  public int modifiers() {
    return modifiers;
  }

  public String genericSignature() {
    return genericType;
  }

  public boolean typeIsGeneric() {
    return !genericType.isEmpty();
  }

  @Override
  public void refresh() {
    name = typeComponent.name();
    modifiers = typeComponent.modifiers();
    genericType = getGenericType(typeComponent.genericSignature());
  }

  protected abstract String getGenericType(String genericSignature);
}
