package com.hagenberg.jarvis.models.entities.classList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.Type;

public class JField implements Refreshable {

  private final Field field;
  private String name;
  private int modifiers;
  private Type type;
  private String typeName;
  private String genericType;
  private Pattern genericTypePattern = Pattern.compile("T([\\w\\d]+);");

  public JField(Field field) {
    this.field = field;
    refresh();
  }

  public String name() {
    return name;
  }

  public int modifiers() {
    return modifiers;
  }

  public Type type() {
    return type;
  }

  public String typeName() {
    return typeName;
  }

  public String genericSignature() {
    return genericType;
  }

  public boolean typeIsGeneric() {
    return !genericType.isEmpty();
  }

  @Override
  public void refresh() {
    name = field.name();
    modifiers = field.modifiers();
    try {
      type = field.type();
    } catch (ClassNotLoadedException e) {
      type = null;
    }
    typeName = field.typeName();
    genericType = getGenericType(field.genericSignature());
  }

  public static List<JField> from(List<Field> allFields) {
    List<JField> fields = new ArrayList<>();
    for (Field field : allFields) {
      fields.add(new JField(field));
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
