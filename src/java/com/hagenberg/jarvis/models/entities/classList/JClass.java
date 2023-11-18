package com.hagenberg.jarvis.models.entities.classList;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.jarvis.models.ClassModel;
import com.hagenberg.jarvis.util.IndexedList;
import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;

public class JClass extends JReferenceType implements Comparable<JClass> {
  
  private final ClassType clazz; 

  private final List<JClass> subClasses = new ArrayList<>();
  private final List<JInterface> interfaces = new ArrayList<>();
  private JClass superClass;

  public JClass(ClassType clazz, ClassModel model) {
    super(clazz, model);
    this.clazz = clazz;
  }

  public List<JClass> subclasses() {
    return subClasses;
  }

  public JClass superclass() {
    return superClass;
  }

  public List<JInterface> interfaces() {
    return interfaces;
  }

  @Override
  public void refresh() {
    super.refresh();
    subClasses.clear();
    for (ClassType subClass : clazz.subclasses()) {
      subClasses.add(model.getOrCreate(subClass));
    }
    interfaces.clear();
    for (InterfaceType iface : clazz.allInterfaces()) {
      interfaces.add(model.getOrCreate(iface));
    }
    if (clazz.superclass() != null) {
      superClass = model.getOrCreate(clazz.superclass());
    }
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

  @Override
  public List<IndexedList<JReferenceType, JField>> allFields() {
    List<IndexedList<JReferenceType, JField>> allFields = new ArrayList<>();

    for (JInterface iface : interfaces()) {
      allFields.add(new IndexedList<>(iface, iface.fields()));
    }

    allFields.add(new IndexedList<>(this, fields()));

    JClass superClass = this.superClass;
    while(superClass != null) {
      allFields.add(new IndexedList<>(superClass, superClass.fields()));
      superClass = superClass.superclass();
    }
  
    return allFields;
  }

  @Override
  public List<IndexedList<JReferenceType, JMethod>> allMethods() {
    List<IndexedList<JReferenceType, JMethod>> allMethods = new ArrayList<>();

    for (JInterface iface : interfaces()) {
      allMethods.add(new IndexedList<>(iface, iface.methods()));
    }

    allMethods.add(new IndexedList<>(this, methods()));

    JClass superClass = this.superClass;
    while(superClass != null) {
      allMethods.add(new IndexedList<>(superClass, superClass.methods()));
      superClass = superClass.superclass();
    }

    return allMethods;
  }
}
