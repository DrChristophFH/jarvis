package com.hagenberg.jarvis.models.entities.wrappers;

import com.hagenberg.jarvis.models.ClassModel;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class JPackage {
  private final ClassModel model;
  private final String name;
  private final SortedMap<String, JPackage> subPackages = new TreeMap<>();
  private final SortedSet<JClassType> classes = new TreeSet<>();
  private final SortedSet<JInterfaceType> interfaces = new TreeSet<>();
  private final SortedSet<JArrayType> arrays = new TreeSet<>();

  public JPackage(String name, ClassModel model) {
    this.model = model;
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
  
  public Iterable<JClassType> getClasses() {
    return classes;
  }

  public void addClass(ClassType clazz) {
    classes.add(model.getOrCreate(clazz));
  }

  public Iterable<JInterfaceType> getInterfaces() {
    return interfaces;
  }

  public void addInterface(InterfaceType iface) {
    interfaces.add(model.getOrCreate(iface));
  }

  public Iterable<JArrayType> getArrays() {
    return arrays;
  }

  public void addArray(ArrayType array) {
    arrays.add(model.getOrCreate(array));
  }
}
