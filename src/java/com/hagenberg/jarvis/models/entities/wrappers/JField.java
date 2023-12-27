package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jdi.Field;

public class JField extends JTypeComponent {

  private final Field field;
  private JType type;
  private Pattern genericTypePattern = Pattern.compile("T([\\w\\d]+);");

  public JField(Field field) {
    super(field);
    this.field = field;
    refresh();
  }

  public JType type() {
    return type;
  }

  public Field getField() {
    return field;
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
