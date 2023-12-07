package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Method;
import com.sun.jdi.Type;

public class JMethod extends JTypeComponent {

  private final Method jdiMethod;
  private List<JLocalVariable> arguments;
  private JType returnType;
  private String returnTypeName;
  private Pattern genericTypePattern = Pattern.compile("\\)(\\[*)T([\\w\\d]+);");

  public JMethod(Method method) {
    super(method);
    this.jdiMethod = method;
    refresh();
  }

  public JType returnType() {
    return returnType;
  }

  public String returnTypeName() {
    return returnTypeName;
  }

  public List<JLocalVariable> arguments() {
    return arguments;
  }

  public Method getJdiMethod() {
    return jdiMethod;
  }

  @Override
  public void refresh() {
    super.refresh();
    try {
      returnType = jdiMethod.returnType();
    } catch (ClassNotLoadedException e) {
      returnType = null;
    }
    returnTypeName = jdiMethod.returnTypeName();
    try {
      arguments = JLocalVariable.from(jdiMethod.arguments());
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
