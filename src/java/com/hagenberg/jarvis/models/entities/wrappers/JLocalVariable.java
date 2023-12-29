package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jdi.LocalVariable;

public class JLocalVariable implements ReferenceHolder {
  private final LocalVariable jdiLocalVariable; // The local variable this graph variable represents
  
  private final String name;
  private final JType type;
  private JValue value;
  private final String genericType;
  private final Pattern genericTypePattern = Pattern.compile("T([\\w\\d]+);");

  public JLocalVariable(LocalVariable localVariable, JType type) {
    this.jdiLocalVariable = localVariable;
    this.type = type;
    this.genericType = getGenericType(jdiLocalVariable.genericSignature());
    this.name = jdiLocalVariable.name();
  }

  public String name() {
    return name;
  }

  public JValue value() {
    return value;
  }

  public void setValue(JValue node) {
    this.value = node;
  }

  public JType getType() {
    return type;
  }

  public String genericTypeName() {
    return genericType;
  }

  public boolean typeIsGeneric() {
    return !genericType.isEmpty();
  }

  public LocalVariable getJdiLocalVariable() {
    return jdiLocalVariable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return Objects.equals(name, ((JLocalVariable) o).name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }

  private String getGenericType(String genericSignature) {
    if (genericSignature == null) { // no generic signature
      return "";
    }

    Matcher matcher = genericTypePattern.matcher(genericSignature);

    if (matcher.matches()) {
      return matcher.group(1);
    }
    
    return "";
  }
}