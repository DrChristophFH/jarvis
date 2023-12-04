package com.hagenberg.jarvis.models.entities.graph;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.Type;

public class ObjectGNode extends GNode {
  private final long objectId; // ID from JDI
  private final List<MemberGVariable> members = new ArrayList<>();
  private final List<GVariable> referenceHolders = new ArrayList<>();
  private String toStringRepresentation = "";

  public ObjectGNode(long id, Type type) {
    super(type);
    this.objectId = id;
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

  public void addReferenceHolder(GVariable referenceHolder) {
    referenceHolders.add(referenceHolder);
  }

  public void removeReferenceHolder(GVariable referenceHolder) {
    referenceHolders.remove(referenceHolder);
  }

  public List<GVariable> getReferenceHolders() {
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
    ObjectGNode other = (ObjectGNode) obj;
    if (objectId != other.objectId) return false;
    return true;
  }

  @Override
  public String toString() {
    return getTypeName() + "#" + objectId;
  }
}