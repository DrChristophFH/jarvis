package com.hagenberg.jarvis.models.entities.graph;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.imgui.Vec2;
import com.hagenberg.jarvis.graph.LayoutableNode;

public class ObjectGNode extends GNode implements LayoutableNode {
  private final long id; // ID from JDI
  private final List<MemberGVariable> members = new ArrayList<>();
  private final List<GVariable> referenceHolders = new ArrayList<>();
  private String toStringRepresentation = "";
  private Vec2 position = new Vec2(0, 0);
  private Vec2 velocity = new Vec2(0, 0);
  private boolean frozen = false;
  private boolean layouted = true;

  public ObjectGNode(long id, String type) {
    super(type);
    this.id = id;
  }

  public String getToString() {
    return toStringRepresentation;
  }

  public void setToString(String toString) {
    this.toStringRepresentation = toString;
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

  public long getId() {
    return id;
  }

  @Override
  public Vec2 getPosition() {
    return position;
  }

  @Override
  public void setPosition(Vec2 position) {
    this.position = position;
  }

  @Override
  public Vec2 getVelocity() {
    return velocity;
  }

  @Override
  public void setVelocity(Vec2 velocity) {
    this.velocity = velocity;
  }

  @Override
  public boolean isFrozen() {
    return frozen;
  }

  @Override
  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
  }

  @Override
  public boolean isLayouted() {
    return layouted;
  }

  @Override
  public Iterable<LayoutableNode> getNeighbors() {
    List<LayoutableNode> neighbors = new ArrayList<>();

    for (MemberGVariable member : members) {
      if (member.getNode() instanceof ObjectGNode obj) {
        neighbors.add(obj);
      }
    }

    for (GVariable referenceHolder : referenceHolders) {
      if (referenceHolder instanceof LocalGVariable lgv) {
        neighbors.add(lgv);
      } else if (referenceHolder instanceof MemberGVariable mgv) {
        neighbors.add(mgv.getContainingObject());
      }
    }

    return neighbors;
  }
}