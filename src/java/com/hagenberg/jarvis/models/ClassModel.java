package com.hagenberg.jarvis.models;

import com.hagenberg.jarvis.models.entities.wrappers.JArrayType;
import com.hagenberg.jarvis.models.entities.wrappers.JClassType;
import com.hagenberg.jarvis.models.entities.wrappers.JInterfaceType;
import com.hagenberg.jarvis.models.entities.wrappers.JMethod;
import com.hagenberg.jarvis.models.entities.wrappers.JPackage;
import com.hagenberg.jarvis.models.entities.wrappers.JPrimitiveType;
import com.hagenberg.jarvis.models.entities.wrappers.JType;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.Type;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class ClassModel {
  private final ReentrantLock lock = new ReentrantLock();

  private Map<String, JPackage> packages = new HashMap<>();
  private HashMap<ClassType, JClassType> classes = new HashMap<>();
  private HashMap<InterfaceType, JInterfaceType> interfaces = new HashMap<>();
  private HashMap<ArrayType, JArrayType> arrays = new HashMap<>();
  private HashMap<PrimitiveType, JPrimitiveType> primitives = new HashMap<>();

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

  public JClassType getOrCreate(ClassType classType) {
    lockModel();
    try {
      JClassType clazz = classes.get(classType);
      if (clazz == null) {
        clazz = new JClassType(classType, this);
        classes.put(classType, clazz);
      }
      return clazz;
    } finally {
      unlockModel();
    }
  }

  public JInterfaceType getOrCreate(InterfaceType iFaceType) {
    lockModel();
    try {
      JInterfaceType iface = interfaces.get(iFaceType);
      if (iface == null) {
        iface = new JInterfaceType(iFaceType, this);
        interfaces.put(iFaceType, iface);
      }
      return iface;
    } finally {
      unlockModel();
    }
  }

  public JArrayType getOrCreate(ArrayType arrayType) {
    lockModel();
    try {
      JArrayType array = arrays.get(arrayType);
      if (array == null) {
        array = new JArrayType(arrayType, this);
        arrays.put(arrayType, array);
      }
      return array;
    } finally {
      unlockModel();
    }
  }

  public JPrimitiveType getOrCreate(PrimitiveType primitiveType) {
    lockModel();
    try {
      JPrimitiveType primitive = primitives.get(primitiveType);
      if (primitive == null) {
        primitive = new JPrimitiveType(primitiveType);
        primitives.put(primitiveType, primitive);
      }
      return primitive;
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

  public JType getJType(Type type) {
    if (type instanceof PrimitiveType primType) {
      return getOrCreate(primType);
    } else if (type instanceof ClassType classType) {
      return getOrCreate(classType);
    } else if (type instanceof ArrayType arrayType) {
      return getOrCreate(arrayType);
    } else if (type instanceof InterfaceType ifaceType) {
      return getOrCreate(ifaceType);
    } else {
      return null;
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

  public JMethod getJMethodInType(JType classType, Method method) {
    if (classType instanceof JClassType clazz) {
      return clazz.getMethod(method);
    } else if (classType instanceof JInterfaceType iface) {
      return iface.getMethod(method);
    } else {
      return null;
    }
  }
}
