package com.hagenberg.jarvis.models.entities.classList;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.Method;

public class JMethod implements Refreshable {
  
  private final Method method;

  public JMethod(Method method) {
    this.method = method;
    refresh();
  }

  @Override
  public void refresh() {
    
  }

  public static List<JMethod> from(List<Method> allMethods) {
    List<JMethod> methods = new ArrayList<>();
    for (Method method : allMethods) {
      methods.add(new JMethod(method));
    }
    return methods;
  }
}
