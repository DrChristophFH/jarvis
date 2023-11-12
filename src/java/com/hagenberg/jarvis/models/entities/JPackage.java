package com.hagenberg.jarvis.models.entities;

import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class JPackage {
  private final String name;
  private final SortedMap<String, JPackage> subPackages = new TreeMap<>();
  private final SortedSet<JClass> classes = new TreeSet<>();
  private final SortedSet<JInterface> interfaces = new TreeSet<>();
  private final SortedSet<JArray> arrays = new TreeSet<>();

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
  
  public Iterable<JClass> getClasses() {
    return classes;
  }

  public void addClass(ClassType clazz) {
    classes.add(new JClass(clazz));
  }

  public Iterable<JInterface> getInterfaces() {
    return interfaces;
  }

  public void addInterface(InterfaceType iface) {
    interfaces.add(new JInterface(iface));
  }

  public Iterable<JArray> getArrays() {
    return arrays;
  }

  public void addArray(ArrayType array) {
    arrays.add(new JArray(array));
  }
}
