package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hagenberg.jarvis.models.ClassModel;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;
import com.sun.jdi.Type;

public class JField extends JTypeComponent {
  private final Field field;

  private final JType type;

  public JField(Field field, JType type) {
    super(field);
    this.field = field;
    this.type = type;
  }

  public JType type() {
    return type;
  }

  public Field getField() {
    return field;
  }

  public static List<JField> from(List<Field> allFields, ClassModel model) {
    List<JField> fields = new ArrayList<>();
    for (Field field : allFields) {
      Type type;
      try {
        type = field.type();
      } catch (ClassNotLoadedException e) {
        System.out.println("Class not loaded: " + e.getMessage());
        type = null;
      }
      fields.add(new JField(field, model.getJType(type)));
    }
    return fields;
  }

  @Override
  protected String getGenericType(String genericSignature) {
    if (genericSignature == null) { // no generic signature
      return "";
    }
    
    Pattern genericTypePattern = Pattern.compile("T([\\w\\d]+);");
    Matcher matcher = genericTypePattern.matcher(genericSignature);

    if (matcher.matches()) {
      return matcher.group(1);
    }

    return "";
  }
}
