package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.Objects;

import com.sun.jdi.LocalVariable;

public class JLocalVariable implements ReferenceHolder {
  private LocalVariable jdiLocalVariable; // The local variable this graph variable represents
  
  private String name;
  private JType type;
  private JValue value;

  public JLocalVariable(LocalVariable localVariable, JType type) {
    this.jdiLocalVariable = localVariable;
    this.type = type;
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
}