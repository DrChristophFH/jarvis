package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;

public class JObjectReference extends JValue implements ReferenceHolder {

  private final ObjectReference jdiObjectReference;

  private final long objectId;
  private final List<JMember> members = new ArrayList<>();
  private final List<ReferenceHolder> referenceHolders = new ArrayList<>();

  private String toStringRepresentation = "";

  public JObjectReference(ObjectReference jdiObjectReference, JReferenceType type) {
    super(type);
    this.jdiObjectReference = jdiObjectReference;
    this.objectId = jdiObjectReference.uniqueID();

    // setup members
    for (Field field : jdiObjectReference.referenceType().allFields()) {
      if (field.isStatic()) continue; // skip static fields

      JField jField = type.getField(field);
      JValue jValue = null;

      JMember member = new JMember(jField, jValue);
      this.addMember(member);
    }
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

  public void addMember(JMember memberVariable) {
    members.add(memberVariable);
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

  public List<JMember> getMembers() {
    return members;
  }

  public JMember getMember(String name) {
    return members.stream().filter(m -> m.field().name().equals(name)).findFirst().orElse(null);
  }

  public void setMember(JField field, JValue value) {
    members.stream().filter(m -> m.field().equals(field)).findFirst().ifPresent(m -> m.setValue(value));
  }

  public JMember getMember(JField field) {
    return members.stream().filter(m -> m.field().equals(field)).findFirst().orElse(null);
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

  public List<JObjectReference> removeAsReferenceHolder() {
    List<JObjectReference> removed = new ArrayList<>();
    for (JMember member : members) {
      if (member.value() instanceof JObjectReference objRef) {
        objRef.removeReferenceHolder(this);
        if (objRef.getReferenceHolders().isEmpty()) {
          removed.add(objRef);
        }
      }
    }
    return removed;
  }
}