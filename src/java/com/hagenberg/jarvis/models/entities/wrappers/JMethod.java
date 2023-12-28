package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hagenberg.jarvis.models.ClassModel;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Method;
import com.sun.jdi.Type;

public class JMethod extends JTypeComponent {

  private final Method jdiMethod;
  private final List<JLocalVariable> arguments = new ArrayList<>();
  private final ClassModel model;
  private JType returnType;
  private boolean typeLoaded = false;

  public JMethod(Method method, ClassModel model) {
    super(method);
    this.jdiMethod = method;
    this.model = model;
  }

  /**
   * @return the JType or null if the underlying type has not been loaded yet
   */
  public JType returnType() {
    if (!typeLoaded) {
      tryResolveType();
    }
    return returnType;
  }

  public void tryResolveType() {
    try {
      Type type = jdiMethod.returnType();
      returnType = type == null ? new JVoidType() : model.getJType(type);
      typeLoaded = true;
    } catch (ClassNotLoadedException e) {
      returnType = new JNotLoadedType(jdiMethod.returnTypeName());
    }
  }

  public List<JLocalVariable> arguments() {
    return arguments;
  }

  public Method getJdiMethod() {
    return jdiMethod;
  }

  public static List<JMethod> from(List<Method> allMethods, ClassModel model) {
    List<JMethod> methods = new ArrayList<>();
    for (Method method : allMethods) {
      methods.add(new JMethod(method, model));
    }
    return methods;
  }

  @Override
  protected String getGenericType(String genericSignature) {
    if (genericSignature == null) { // no generic signature
      return "";
    }

    Pattern genericTypePattern = Pattern.compile("\\)(\\[*)T([\\w\\d]+);");
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
