package com.hagenberg.jarvis.models.entities.classList;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Method;
import com.sun.jdi.Type;

public class JMethod implements Refreshable {
  
  private final Method method;
  private List<JLocalVariable> arguments;
  private String name;
  private Type returnType;
  private String returnTypeName;
  private int modifiers;

  public JMethod(Method method) {
    this.method = method;
    refresh();
  }

  public String name() {
    return name;
  }

  public Type returnType() {
    return returnType;
  }

  public int modifiers() {
    return modifiers;
  }

  public String returnTypeName() {
    return returnTypeName;
  }

  public List<JLocalVariable> arguments() {
    return arguments;
  }

  @Override
  public void refresh() {
    name = method.name();
    try {
      returnType = method.returnType();
    } catch (ClassNotLoadedException e) {
      returnType = null;
    } 
    modifiers = method.modifiers();
    returnTypeName = method.returnTypeName();
    try {
      arguments = JLocalVariable.from(method.arguments());
    } catch (AbsentInformationException e) {
      arguments = new ArrayList<>();
    }
  }

  public static List<JMethod> from(List<Method> allMethods) {
    List<JMethod> methods = new ArrayList<>();
    for (Method method : allMethods) {
      methods.add(new JMethod(method));
    }
    return methods;
  }
}
