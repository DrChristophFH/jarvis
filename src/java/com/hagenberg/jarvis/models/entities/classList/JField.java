package com.hagenberg.jarvis.models.entities.classList;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.Field;

public class JField implements Refreshable {
  
  private final Field field;

  public JField(Field field) {
    this.field = field;
  }

  @Override
  public void refresh() {
    
  }

  public static List<JField> from(List<Field> allFields) {
    List<JField> fields = new ArrayList<>();
    for (Field field : allFields) {
      fields.add(new JField(field));
    }
    return fields;
  }
}
