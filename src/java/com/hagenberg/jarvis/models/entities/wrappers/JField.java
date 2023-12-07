package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.Type;

public class JField extends JTypeComponent {

  private final Field field;
  private Type type;
  private String typeName;
  private Pattern genericTypePattern = Pattern.compile("T([\\w\\d]+);");

  public JField(Field field) {
    super(field);
    this.field = field;
    refresh();
  }

  public Type type() {
    return type;
  }

  public String typeName() {
    return typeName;
  }

  @Override
  public void refresh() {
    super.refresh();
    try {
      type = field.type();
    } catch (ClassNotLoadedException e) {
      type = null;
    }
    typeName = field.typeName();
  }

  public static List<JField> from(List<Field> allFields) {
    List<JField> fields = new ArrayList<>();
    for (Field field : allFields) {
      fields.add(new JField(field));
    }
    return fields;
  }

  @Override
  protected String getGenericType(String genericSignature) {
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
