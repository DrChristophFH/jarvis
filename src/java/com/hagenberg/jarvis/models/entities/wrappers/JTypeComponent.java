package com.hagenberg.jarvis.models.entities.wrappers;

import com.hagenberg.jarvis.models.entities.AccessModifier;
import com.sun.jdi.TypeComponent;

public abstract class JTypeComponent implements Refreshable {
  private final TypeComponent typeComponent;
  private String name;
  private AccessModifier modifier;
  private String genericType;

  public JTypeComponent(TypeComponent typeComponent) {
    this.typeComponent = typeComponent;
  }

  public String name() {
    return name;
  }

  public AccessModifier modifiers() {
    return modifier;
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
    modifier = new AccessModifier(typeComponent.modifiers());
    genericType = getGenericType(typeComponent.genericSignature());
  }

  protected abstract String getGenericType(String genericSignature);
}
