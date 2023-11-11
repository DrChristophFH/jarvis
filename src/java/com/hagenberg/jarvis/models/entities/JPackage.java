package com.hagenberg.jarvis.models.entities;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class JPackage {
  private class ReferenceTypeComparator implements Comparator<ReferenceType> {
    @Override
    public int compare(ReferenceType o1, ReferenceType o2) {
      // lexicographical order
      return o1.name().compareTo(o2.name());
    }
  }

  private final String name;
  private final SortedMap<String, JPackage> subPackages = new TreeMap<>();
  private final SortedSet<ClassType> classes = new TreeSet<>(new ReferenceTypeComparator());
  private final SortedSet<InterfaceType> interfaces = new TreeSet<>(new ReferenceTypeComparator());
  private final SortedSet<ArrayType> arrays = new TreeSet<>(new ReferenceTypeComparator());

  public JPackage(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
  
  public SortedMap<String, JPackage> getSubPackages() {
    return subPackages;
  }

  public void addSubPackage(JPackage subPackage) {
    subPackages.put(subPackage.name, subPackage);
  }
  
  public Iterable<ClassType> getClasses() {
    return classes;
  }

  public void addClass(ClassType clazz) {
    classes.add(clazz);
  }

  public Iterable<InterfaceType> getInterfaces() {
    return interfaces;
  }

  public void addInterface(InterfaceType iface) {
    interfaces.add(iface);
  }

  public Iterable<ArrayType> getArrays() {
    return arrays;
  }

  public void addArray(ArrayType array) {
    arrays.add(array);
  }
}
