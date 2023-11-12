package com.hagenberg.jarvis.models.entities;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;

public class JClass extends JReferenceType implements Comparable<JClass> {
  
  private final ClassType clazz; 

  private final List<ClassType> subClasses = new ArrayList<>();
  private final List<InterfaceType> interfaces = new ArrayList<>();
  private ClassType superClass;

  public JClass(ClassType clazz) {
    super(clazz);
    this.clazz = clazz;
    refresh();
  }

  public List<ClassType> subclasses() {
    return subClasses;
  }

  public ClassType superclass() {
    return superClass;
  }

  public List<InterfaceType> interfaces() {
    return interfaces;
  }

  @Override
  public void refresh() {
    super.refresh();
    subClasses.clear();
    subClasses.addAll(clazz.subclasses());
    interfaces.clear();
    interfaces.addAll(clazz.allInterfaces());
    superClass = clazz.superclass();
  }

  @Override
  public int compareTo(JClass o) {
    return name().compareTo(o.name());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    JClass other = (JClass) obj;
    if (clazz == null) {
      if (other.clazz != null) return false;
    } else if (!clazz.equals(other.clazz)) return false;
    return true;
  }
}
