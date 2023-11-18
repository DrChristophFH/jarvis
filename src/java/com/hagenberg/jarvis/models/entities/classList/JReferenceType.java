package com.hagenberg.jarvis.models.entities.classList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hagenberg.jarvis.models.ClassModel;
import com.hagenberg.jarvis.util.IndexedList;
import com.sun.jdi.ReferenceType;

public abstract class JReferenceType implements Refreshable {

  protected final ClassModel model;
  private final ReferenceType referenceType;
  private final List<JField> fields = new ArrayList<>();
  private final List<JMethod> methods = new ArrayList<>();
  private final Pattern genericSignaturePattern = Pattern.compile("([\\w\\d]+):");
  private String name;
  private String genericTypeParameters;

  public JReferenceType(ReferenceType referenceType, ClassModel model) {
    this.model = model;
    this.referenceType = referenceType;
  }

  public ReferenceType getReferenceType() {
    return referenceType;
  }

  public List<JField> fields() {
    return fields;
  }

  public List<JMethod> methods() {
    return methods;
  }

  public abstract List<IndexedList<JReferenceType, JField>> allFields();

  public abstract List<IndexedList<JReferenceType, JMethod>> allMethods();

  public String name() {
    return name;
  }

  public String genericSignature() {
    return genericTypeParameters;
  }

  public boolean isAbstract() {
    return referenceType.isAbstract();
  }

  public boolean isFinal() {
    return referenceType.isFinal();
  }

  public boolean isPrepared() {
    return referenceType.isPrepared();
  }

  public boolean isStatic() {
    return referenceType.isStatic();
  }

  /**
   * Requeries all the information from the debuggee.
   */
  public void refresh() {
    fields.clear();
    fields.addAll(JField.from(referenceType.fields()));
    methods.clear();
    methods.addAll(JMethod.from(referenceType.methods()));
    name = referenceType.name();
    genericTypeParameters = getGenericTypeParameters(referenceType.genericSignature());
  }

  /**
   * Converts a generic signature string as specified in the Java Virtual Machine Specification
   * (https://docs.oracle.com/javase/specs/jvms/se18/html/jvms-4.html#jvms-4.7.9) to a simplified version.
   * 
   * @param genericSignature the generic signature string to convert
   * @return a simplified generic type parameter string like <T, U> or <?>
   */
  private String getGenericTypeParameters(String genericSignature) {
    if (genericSignature == null) { // no generic signature
      return "";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("<");

    Matcher matcher = genericSignaturePattern.matcher(genericSignature);

    boolean isFirst = true;
    while (matcher.find()) {
      if (!isFirst) {
        sb.append(", ");
      }
      sb.append(matcher.group(1));
      isFirst = false;
    }

    sb.append(">");

    if (isFirst) { // no generic type parameters
      return "";
    }

    return sb.toString();
  }
}
