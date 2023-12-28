package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.jarvis.models.ClassModel;
import com.hagenberg.jarvis.util.IndexedList;
import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.Method;

public class JClassType extends JReferenceType implements Comparable<JClassType> {
  
  private final ClassType clazz; 

  private final List<JClassType> subClasses = new ArrayList<>();
  private final List<JInterfaceType> interfaces = new ArrayList<>();
  private JClassType superClass;

  public JClassType(ClassType clazz, ClassModel model) {
    super(clazz, model);
    this.clazz = clazz;
    for (ClassType subClass : clazz.subclasses()) {
      subClasses.add(model.getOrCreate(subClass));
    }
    for (InterfaceType iface : clazz.allInterfaces()) {
      interfaces.add(model.getOrCreate(iface));
    }
    if (clazz.superclass() != null) {
      superClass = model.getOrCreate(clazz.superclass());
    }
  }

  public List<JClassType> subclasses() {
    return subClasses;
  }

  public JClassType superclass() {
    return superClass;
  }

  public List<JInterfaceType> interfaces() {
    return interfaces;
  }

  @Override
  public int compareTo(JClassType o) {
    return name().compareTo(o.name());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    JClassType other = (JClassType) obj;
    if (clazz == null) {
      if (other.clazz != null) return false;
    } else if (!clazz.equals(other.clazz)) return false;
    return true;
  }

  @Override
  public List<IndexedList<JReferenceType, JField>> allFields() {
    List<IndexedList<JReferenceType, JField>> allFields = new ArrayList<>();

    for (JInterfaceType iface : interfaces()) {
      allFields.add(new IndexedList<>(iface, iface.fields()));
    }

    allFields.add(new IndexedList<>(this, fields()));

    JClassType superClass = this.superClass;
    while(superClass != null) {
      allFields.add(new IndexedList<>(superClass, superClass.fields()));
      superClass = superClass.superclass();
    }
  
    return allFields;
  }

  @Override
  public List<IndexedList<JReferenceType, JMethod>> allMethods() {
    List<IndexedList<JReferenceType, JMethod>> allMethods = new ArrayList<>();

    for (JInterfaceType iface : interfaces()) {
      allMethods.add(new IndexedList<>(iface, iface.methods()));
    }

    allMethods.add(new IndexedList<>(this, methods()));

    JClassType superClass = this.superClass;
    while(superClass != null) {
      allMethods.add(new IndexedList<>(superClass, superClass.methods()));
      superClass = superClass.superclass();
    }

    return allMethods;
  }

  @Override
  public JMethod getMethod(Method jdiMethod) {
    for (JMethod method : methods()) {
      if (method.getJdiMethod().equals(jdiMethod)) {
        return method;
      }
    }
    return null;
  }
}
