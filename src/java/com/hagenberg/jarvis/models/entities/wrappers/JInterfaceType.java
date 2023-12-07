package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.jarvis.models.ClassModel;
import com.hagenberg.jarvis.util.IndexedList;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.Method;

public class JInterfaceType extends JReferenceType implements Comparable<JInterfaceType> {

  private final InterfaceType iface;
  private final ClassModel model;

  private final List<JInterfaceType> superInterfaces = new ArrayList<>();
  private final List<JInterfaceType> subInterfaces = new ArrayList<>();

  public JInterfaceType(InterfaceType iface, ClassModel model) {
    super(iface);
    this.iface = iface;
    this.model = model;
  }

  public List<JInterfaceType> superinterfaces() {
    return superInterfaces;
  }

  public List<JInterfaceType> subinterfaces() {
    return subInterfaces;
  }

  @Override
  public void refresh() {
    super.refresh();
    superInterfaces.clear();
    for (InterfaceType superInterface : iface.superinterfaces()) {
      superInterfaces.add(model.getOrCreate(superInterface));
    }
    subInterfaces.clear();
    for (InterfaceType subInterface : iface.subinterfaces()) {
      subInterfaces.add(model.getOrCreate(subInterface));
    }
  }

  @Override
  public int compareTo(JInterfaceType o) {
    return name().compareTo(o.name());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    JInterfaceType other = (JInterfaceType) obj;
    if (iface == null) {
      if (other.iface != null) return false;
    } else if (!iface.equals(other.iface)) return false;
    return true;
  }

  @Override
  public List<IndexedList<JReferenceType, JField>> allFields() {
    List<IndexedList<JReferenceType, JField>> allFields = new ArrayList<>();

    for (JInterfaceType superInterface : superInterfaces) {
      allFields.add(new IndexedList<>(superInterface, superInterface.fields()));
    }

    allFields.add(new IndexedList<>(this, fields()));

    return allFields;
  }

  @Override
  public List<IndexedList<JReferenceType, JMethod>> allMethods() {
    List<IndexedList<JReferenceType, JMethod>> allMethods = new ArrayList<>();

    for (JInterfaceType superInterface : superInterfaces) {
      allMethods.add(new IndexedList<>(superInterface, superInterface.methods()));
    }

    allMethods.add(new IndexedList<>(this, methods()));

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