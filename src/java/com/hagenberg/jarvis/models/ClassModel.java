package com.hagenberg.jarvis.models;

import com.hagenberg.jarvis.models.entities.wrappers.JArray;
import com.hagenberg.jarvis.models.entities.wrappers.JClass;
import com.hagenberg.jarvis.models.entities.wrappers.JInterface;
import com.hagenberg.jarvis.models.entities.wrappers.JPackage;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.ReferenceType;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ClassModel {
  private final ReentrantLock lock = new ReentrantLock();

  private Map<String, JPackage> packages = new HashMap<>();
  private HashMap<ClassType, JClass> classes = new HashMap<>();
  private HashMap<InterfaceType, JInterface> interfaces = new HashMap<>();
  private HashMap<ArrayType, JArray> arrays = new HashMap<>();

  public void lockModel() {
    lock.lock();
  }

  public void unlockModel() {
    lock.unlock();
  }

  public boolean tryLock(long timeout, TimeUnit unit) {
    try {
      return lock.tryLock(timeout, unit);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // set the interrupt flag
      return false;
    }
  }

  public JClass getOrCreate(ClassType classType) {
    lockModel();
    try {
      JClass clazz = classes.get(classType);
      if (clazz == null) {
        clazz = new JClass(classType, this);
        classes.put(classType, clazz);
        clazz.refresh();
      }
      return clazz;
    } finally {
      unlockModel();
    } 
  }

  public JInterface getOrCreate(InterfaceType iFaceType) {
    lockModel();
    try {
      JInterface iface = interfaces.get(iFaceType);
      if (iface == null) {
        iface = new JInterface(iFaceType, this);
        interfaces.put(iFaceType, iface);
        iface.refresh();
      }
      return iface;
    } finally {
      unlockModel();
    }
  }

  public JArray getOrCreate(ArrayType arrayType) {
    lockModel();
    try {
      JArray array = arrays.get(arrayType);
      if (array == null) {
        array = new JArray(arrayType);
        arrays.put(arrayType, array);
        array.refresh();
      }
      return array;
    } finally {
      unlockModel();
    }
  }

  public void addFromRefType(ReferenceType refType) {
    lockModel();
    try {
      String[] nameParts = refType.name().split("\\.");

      Map<String, JPackage> searchPackages = packages;
      JPackage currentPackage = null;

      for (int i = 0; i < nameParts.length - 1; i++) {
        String namePart = nameParts[i];
        currentPackage = searchPackages.get(namePart);
        if (currentPackage == null) { // from here on, we need to create new packages
          currentPackage = scaffoldPackages(searchPackages, nameParts, i);
          break;
        }
        searchPackages = currentPackage.getSubPackages();
      }

      if (refType instanceof ClassType classType) {
        currentPackage.addClass(classType);
      } else if (refType instanceof InterfaceType ifaceType) {
        currentPackage.addInterface(ifaceType);
      } else if (refType instanceof ArrayType arrayType) {
        currentPackage.addArray(arrayType);
      }
    } finally {
      unlockModel();
    }
  }

  private JPackage scaffoldPackages(Map<String, JPackage> searchPackages, String[] nameParts, int i) {
    JPackage currentPackage;
    for (int j = i; j < nameParts.length - 1; j++) {
      String namePart = nameParts[j];
      currentPackage = new JPackage(namePart, this);
      searchPackages.put(namePart, currentPackage);
      searchPackages = currentPackage.getSubPackages();
    }
    currentPackage = new JPackage(nameParts[nameParts.length - 1], this);
    searchPackages.put(nameParts[nameParts.length - 1], currentPackage);
    return currentPackage;
  }

  public Map<String, JPackage> getRootPackages() {
    return packages;
  }
}
