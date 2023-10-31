package com.hagenberg.jarvis.models;

import com.hagenberg.jarvis.models.entities.graph.*;
import com.hagenberg.jarvis.util.Observable;
import com.hagenberg.jarvis.util.Observer;
import com.sun.jdi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ObjectGraphModel implements Observable {

  private final List<Observer> observers = new ArrayList<>();

  private final List<LocalGVariable> localVars = new ArrayList<>(); // The roots are the local variables visible
  private final Map<Long, ObjectGNode> objectMap = new HashMap<>(); // maps object ids to graph objects

  public List<LocalGVariable> getLocalVars() {
    return localVars;
  }

  public List<ObjectGNode> getObjects() {
    synchronized (objectMap) {
      return new ArrayList<>(objectMap.values());
    }
  }

  public void addLocalVariable(String varName, Value varValue, String staticType, StackFrameInformation sfInfo) {
    LocalGVariable newVar = new LocalGVariable(varName, staticType, sfInfo);

    if (varValue instanceof ObjectReference objRef) {
      newVar.setNode(lookUpObjectNode(objRef, newVar));
    } else if (varValue instanceof PrimitiveValue primValue) {
      newVar.setNode(createPrimitiveNode(primValue));
    }

    synchronized (localVars) {
      localVars.add(newVar);
    }
    notifyObservers();
  }

  @Override
  public void addObserver(Observer observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers() {
    for (Observer observer : observers) {
      observer.update();
    }
  }
  
  public void clear() {
    synchronized (localVars) {
      localVars.clear();
    }
    synchronized (objectMap) {
      objectMap.clear();
    }
    notifyObservers();
  }

  private ObjectGNode lookUpObjectNode(ObjectReference objRef, GVariable referenceHolder) {
    Long id = objRef.uniqueID();
    ObjectGNode existingNode = objectMap.get(id);

    if (existingNode == null) {
      if (objRef instanceof ArrayReference arrayRef) {
        existingNode = createArrayNode(arrayRef);
      } else {
        existingNode = createObjectNode(objRef);
      }
    }

    objectMap.put(id, existingNode);
    existingNode.addReferenceHolder(referenceHolder);
    return existingNode;
  }

  private ObjectGNode createObjectNode(ObjectReference objRef) {
    ObjectGNode newNode = new ObjectGNode(objRef.uniqueID(), objRef.referenceType().name());
    for (Field field : objRef.referenceType().fields()) {
      if (field.isStatic()) continue; // skip static fields

      Value fieldValue = objRef.getValue(field);
      MemberGVariable member = createMemberGVariable(newNode, field.name(), field.typeName(), fieldValue, field.modifiers());
      newNode.addMember(member);
    }
    return newNode;
  }

  private ObjectGNode createArrayNode(ArrayReference arrayRef) {
    ArrayGNode newNode = new ArrayGNode(arrayRef.uniqueID(), arrayRef.referenceType().name());
    List<Value> values = arrayRef.getValues();
    for (int i = 0; i < values.size(); i++) {
      Value value = values.get(i);
      MemberGVariable arrayMember = createMemberGVariable(newNode, "[" + i + "]", newNode.getType(), value, 0);
      newNode.addContent(arrayMember);
    }
    return newNode;
  }

  private GNode createPrimitiveNode(PrimitiveValue primValue) {
    return new PrimitiveGNode(primValue.type().toString(), primValue.toString());
  }

  private MemberGVariable createMemberGVariable(ObjectGNode parent, String name, String staticType, Value value, int accessModifier) {
    MemberGVariable member = new MemberGVariable(name, staticType, parent, accessModifier);

    if (value instanceof ObjectReference objRef) {
      member.setNode(lookUpObjectNode(objRef, member));
    } else if (value instanceof PrimitiveValue primValue) {
      member.setNode(createPrimitiveNode(primValue));
    }

    return member;
  }
}
