package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hagenberg.jarvis.models.ClassModel;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Field;

public class JField extends JTypeComponent {
  private final Field field;

  private JType type;
  private final ClassModel model;

  public JField(Field field, ClassModel model) {
    super(field);
    this.field = field;
    this.model = model;
  }


  /**
   * @return the JType or null if the underlying type has not been loaded yet
   */
  public JType type() {
    if (type == null) {
      try {
        type = model.getJType(field.type());
      } catch (ClassNotLoadedException e) {
      }
    }
    return type;
  }

  public Field getField() {
    return field;
  }

  public static List<JField> from(List<Field> allFields, ClassModel model) {
    List<JField> fields = new ArrayList<>();
    for (Field field : allFields) {
      fields.add(new JField(field, model));
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
