package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jdi.ObjectReference;

public class JObjectReference extends JValue {

  private final ObjectReference jdiObjectReference;

  private final Map<JField, JValue> members = new HashMap<>();
  private final List<ReferenceHolder> referenceHolders = new ArrayList<>();
  private long objectId = -1;
  private String toStringRepresentation = "";

  public JObjectReference(ObjectReference jdiObjectReference, JType type) {
    super(type);
    this.jdiObjectReference = jdiObjectReference;
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

  public void addMember(MemberGVariable memberVariable) {
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

  public List<MemberGVariable> getMembers() {
    return members;
  }

  public MemberGVariable getMember(String name) {
    return members.stream().filter(m -> m.getName().equals(name)).findFirst().orElse(null);
  }

  public long getObjectId() {
    return objectId;
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
  public void refresh() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'refresh'");
  }
}