package com.hagenberg.jarvis.models.entities.classList;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.jarvis.models.ClassModel;
import com.hagenberg.jarvis.util.IndexedList;
import com.sun.jdi.ArrayType;

public class JArray extends JReferenceType implements Comparable<JArray> {

  private final ArrayType array;

  public JArray(ArrayType array, ClassModel model) {
    super(array, model);
    this.array = array;
  }

  @Override
  public int compareTo(JArray o) {
    return name().compareTo(o.name());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    JArray other = (JArray) obj;
    if (array == null) {
      if (other.array != null) return false;
    } else if (!array.equals(other.array)) return false;
    return true;
  }

  @Override
  public List<IndexedList<JReferenceType, JField>> allFields() {
    return new ArrayList<>();
  }

  @Override
  public List<IndexedList<JReferenceType, JMethod>> allMethods() {
    return new ArrayList<>();
  }
}
