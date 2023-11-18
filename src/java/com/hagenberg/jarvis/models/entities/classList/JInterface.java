package com.hagenberg.jarvis.models.entities.classList;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.jarvis.models.ClassModel;
import com.hagenberg.jarvis.util.IndexedList;
import com.sun.jdi.InterfaceType;

public class JInterface extends JReferenceType implements Comparable<JInterface> {

  private final InterfaceType iface;

  private final List<JInterface> superInterfaces = new ArrayList<>();
  private final List<JInterface> subInterfaces = new ArrayList<>();

  public JInterface(InterfaceType iface, ClassModel model) {
    super(iface, model);
    this.iface = iface;
  }

  public List<JInterface> superinterfaces() {
    return superInterfaces;
  }

  public List<JInterface> subinterfaces() {
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
  public int compareTo(JInterface o) {
    return name().compareTo(o.name());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    JInterface other = (JInterface) obj;
    if (iface == null) {
      if (other.iface != null) return false;
    } else if (!iface.equals(other.iface)) return false;
    return true;
  }

  @Override
  public List<IndexedList<JReferenceType, JField>> allFields() {
    List<IndexedList<JReferenceType, JField>> allFields = new ArrayList<>();

    for (JInterface superInterface : superInterfaces) {
      allFields.add(new IndexedList<>(superInterface, superInterface.fields()));
    }

    allFields.add(new IndexedList<>(this, fields()));

    return allFields;
  }

  @Override
  public List<IndexedList<JReferenceType, JMethod>> allMethods() {
    List<IndexedList<JReferenceType, JMethod>> allMethods = new ArrayList<>();

    for (JInterface superInterface : superInterfaces) {
      allMethods.add(new IndexedList<>(superInterface, superInterface.methods()));
    }

    allMethods.add(new IndexedList<>(this, methods()));

    return allMethods;
  }
}
