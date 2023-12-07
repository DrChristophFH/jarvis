package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jdi.ObjectReference;

public class JObjectReference extends JValue implements ReferenceHolder {

  private final ObjectReference jdiObjectReference;

  private final Map<JField, JValue> members = new HashMap<>();
  private final List<ReferenceHolder> referenceHolders = new ArrayList<>();
  private long objectId;
  private String toStringRepresentation = "";

  public JObjectReference(ObjectReference jdiObjectReference, JType type) {
    super(type);
    this.jdiObjectReference = jdiObjectReference;
    this.objectId = jdiObjectReference.uniqueID();
  }

  public String getToString() {
    return toStringRepresentation;
  }

  public void setToString(String toString) {
    if (toString != null) {
      this.toStringRepresentation = toString;
    } else {
      System.out.println("Warning: toString() of " + getTypeName() + " is null");
      this.toStringRepresentation = this.toString();
    }
  }

  public void addMember(JField field, JValue value) {
    members.put(field, value);
  }

  public void addReferenceHolder(ReferenceHolder referenceHolder) {
    referenceHolders.add(referenceHolder);
  }

  public void removeReferenceHolder(ReferenceHolder referenceHolder) {
    referenceHolders.remove(referenceHolder);
  }

  public List<ReferenceHolder> getReferenceHolders() {
    return referenceHolders;
  }

  public Map<JField, JValue> getMembers() {
    return members;
  }

  public Collection<JValue> getValues() {
    return members.values();
  }

  public JValue getMember(JField field) {
    return members.get(field);
  }

  public void setMember(JField field, JValue value) {
    members.put(field, value);
  }

  public ObjectReference getJdiObjectReference() {
    return jdiObjectReference;
  }

  public long getObjectId() {
    return objectId;
  }

  public JReferenceType referenceType() {
    return (JReferenceType) type;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(objectId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    JObjectReference other = (JObjectReference) obj;
    if (objectId != other.objectId) return false;
    return true;
  }

  @Override
  public String toString() {
    return getTypeName() + "#" + objectId;
  }

  @Override
  public String name() {
    return "Object#" + objectId;
  }
}