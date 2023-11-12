package com.hagenberg.jarvis.models.entities;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.InterfaceType;

public class JInterface extends JReferenceType implements Comparable<JInterface> {

  private final InterfaceType iface;

  private final List<InterfaceType> superInterfaces = new ArrayList<>();
  private final List<InterfaceType> subInterfaces = new ArrayList<>();

  public JInterface(InterfaceType iface) {
    super(iface);
    this.iface = iface;
    refresh();
  }

  public List<InterfaceType> superinterfaces() {
    return superInterfaces;
  }

  public List<InterfaceType> subinterfaces() {
    return subInterfaces;
  }

  @Override
  public void refresh() {
    super.refresh();
    superInterfaces.clear();
    superInterfaces.addAll(iface.superinterfaces());
    subInterfaces.clear();
    subInterfaces.addAll(iface.subinterfaces());
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
}
