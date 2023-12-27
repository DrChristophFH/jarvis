package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jdi.Method;

public class JMethod extends JTypeComponent {

  private final Method jdiMethod;
  private List<JLocalVariable> arguments;
  private JType returnType;
  private Pattern genericTypePattern = Pattern.compile("\\)(\\[*)T([\\w\\d]+);");

  public JMethod(Method method) {
    super(method);
    this.jdiMethod = method;
    refresh();
  }

  public JType returnType() {
    return returnType;
  }

  public List<JLocalVariable> arguments() {
    return arguments;
  }

  public Method getJdiMethod() {
    return jdiMethod;
  }

  public static List<JMethod> from(List<Method> allMethods) {
    List<JMethod> methods = new ArrayList<>();
    for (Method method : allMethods) {
      methods.add(new JMethod(method));
    }
    return methods;
  }

  @Override
  protected String getGenericType(String genericSignature) {
    if (genericSignature == null) { // no generic signature
      return "";
    }

    Matcher matcher = genericTypePattern.matcher(genericSignature);

    int arrayDimensions = 0;
    StringBuilder sb = new StringBuilder();

    if (matcher.find()) {
      arrayDimensions = matcher.group(1) == null ? 0 : matcher.group(1).length();
      sb.append(matcher.group(2));
      for (int i = 0; i < arrayDimensions; i++) {
        sb.append("[]");
      }
    }

    return sb.toString();
  }
}
