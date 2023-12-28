package com.hagenberg.jarvis.models.entities.wrappers;

import com.hagenberg.jarvis.models.entities.AccessModifier;
import com.sun.jdi.TypeComponent;

public abstract class JTypeComponent {
  private final TypeComponent typeComponent;
  
  private final String name;
  private final AccessModifier modifier;
  private final String genericType;

  public JTypeComponent(TypeComponent typeComponent) {
    this.typeComponent = typeComponent;
    this.name = typeComponent.name();
    this.modifier = new AccessModifier(typeComponent.modifiers());
    this.genericType = getGenericType(typeComponent.genericSignature());
  }

  public TypeComponent typeComponent() {
    return typeComponent;
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

  protected abstract String getGenericType(String genericSignature);
}
