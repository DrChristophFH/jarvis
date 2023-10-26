package com.hagenberg.jarvis.models;

import com.hagenberg.jarvis.models.entities.graph.*;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.sun.jdi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ObjectGraphModel {
  // The roots are the local variables visible
  private final List<LocalGVariable> nodes = new ArrayList<>();
  private final Map<Long, ObjectGNode> objectMap = new HashMap<>(); // maps object ids to graph objects

  public void addLocalVariable(LocalGVariable localVariable) {
    nodes.add(localVariable);
  }

  public List<LocalGVariable> getNodes() {
    return nodes;
  }

  public GNode getNodeFromValue(Value value) {
    if (value instanceof ObjectReference objRef) {
      Long id = objRef.uniqueID();
      ObjectGNode existingNode = objectMap.get(id);

      if (existingNode == null) {
        if (objRef instanceof ArrayReference arrayRef) {
          existingNode = createArrayNode(arrayRef);
        } else {
          existingNode = createObjectNode(objRef);
        }
        objectMap.put(id, existingNode);
      }

      return new ReferenceGNode(existingNode);
    } else if (value instanceof PrimitiveValue primValue) {
      return createPrimitiveNode(primValue);
    }
    return null;
  }

  private ObjectGNode createObjectNode(ObjectReference objRef) {
    ObjectGNode newNode = new ObjectGNode(objRef.uniqueID(), objRef.referenceType().name());
    for (Field field : objRef.referenceType().fields()) {
      if (field.isStatic()) continue; // skip static fields
      Value fieldValue = objRef.getValue(field);
      newNode.addMember(new MemberGVariable(field.name(), getNodeFromValue(fieldValue), field.modifiers()));
    }
    return newNode;
  }

  private ObjectGNode createArrayNode(ArrayReference arrayRef) {
    ArrayGNode newNode = new ArrayGNode(arrayRef.uniqueID(), arrayRef.referenceType().name());
    for (Value value : arrayRef.getValues()) {
      newNode.addContent(getNodeFromValue(value));
    }
    return newNode;
  }

  private GNode createPrimitiveNode(PrimitiveValue primValue) {
    // create and return a new PrimitiveNode from the primValue
    return new PrimitiveGNode(primValue.type().toString(), primValue.toString());
  }
}
