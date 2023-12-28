package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hagenberg.jarvis.models.ClassModel;
import com.hagenberg.jarvis.util.IndexedList;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;

public abstract class JReferenceType extends JType {
  private final ReferenceType jdiReferenceType;

  private final List<JField> fields = new ArrayList<>();
  private final List<JMethod> methods = new ArrayList<>();
  
  private final Pattern genericSignaturePattern = Pattern.compile("([\\w\\d]+):");
  private String genericTypeParameters;

  public JReferenceType(ReferenceType referenceType, ClassModel model) {
    super(referenceType);
    this.jdiReferenceType = referenceType;
    this.genericTypeParameters = getGenericTypeParameters(jdiReferenceType.genericSignature());
    fields.addAll(JField.from(referenceType.fields(), model));
    methods.addAll(JMethod.from(referenceType.methods(), model));
  }

  public ReferenceType getJdiReferenceType() {
    return jdiReferenceType;
  }

  public List<JField> fields() {
    return fields;
  }

  public List<JMethod> methods() {
    return methods;
  }

  public abstract List<IndexedList<JReferenceType, JField>> allFields();

  public abstract List<IndexedList<JReferenceType, JMethod>> allMethods();

  public abstract JMethod getMethod(Method jdiMethod);

  public boolean isAbstract() {
    return jdiReferenceType.isAbstract();
  }

  public boolean isFinal() {
    return jdiReferenceType.isFinal();
  }

  public boolean isPrepared() {
    return jdiReferenceType.isPrepared();
  }

  public boolean isStatic() {
    return jdiReferenceType.isStatic();
  }

  public String genericSignature() {
    return genericTypeParameters;
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
