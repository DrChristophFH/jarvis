package com.hagenberg.jarvis.models.entities.classList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Type;

public class JLocalVariable implements Refreshable {
  private final LocalVariable localVariable;
  private String name;
  private Type type;
  private String typeName;
  private String genericType;
  private Pattern genericTypePattern = Pattern.compile("T([\\w\\d]+);");

  public JLocalVariable(LocalVariable localVariable) {
    this.localVariable = localVariable;
    refresh();
  }

  public String name() {
    return name;
  }

  public Type type() {
    return type;
  }

  public String typeName() {
    return typeName;
  }

  public String genericTypeName() {
    return genericType;
  }

  public boolean typeIsGeneric() {
    return !genericType.isEmpty();
  }

  @Override
  public void refresh() {
    name = localVariable.name();
    try {
      type = localVariable.type();
    } catch (ClassNotLoadedException e) {
      type = null;
    }
    typeName = localVariable.typeName();
    genericType = getGenericType(localVariable.genericSignature());
  }

  public static List<JLocalVariable> from(List<LocalVariable> allFields) {
    List<JLocalVariable> fields = new ArrayList<>();
    for (LocalVariable field : allFields) {
      fields.add(new JLocalVariable(field));
    }
    return fields;
  }

  private String getGenericType(String genericSignature) {
    if (genericSignature == null) { // no generic signature
      return "";
    }

    Matcher matcher = genericTypePattern.matcher(genericSignature);

    if (matcher.matches()) {
      return matcher.group(1);
    }
    
    return "";
  }
}
