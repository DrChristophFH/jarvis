package com.hagenberg.jarvis.models;

import com.hagenberg.jarvis.models.entities.graph.*;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.sun.jdi.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class ObjectGraphModel {
    // The roots are the local variables visible
    private final ObservableList<LocalGVariable> nodes = FXCollections.observableArrayList();
    private final Map<Long, ObjectGNode> objectMap = new HashMap<>(); // maps object ids to graph objects

    public void addLocalVariable(LocalGVariable localVariable) {
        nodes.add(localVariable);
    }

    public ObservableList<LocalGVariable> getNodes() {
        return nodes;
    }

    public GNode getNodeFromValue(Value value) {
        if (value instanceof ObjectReference objRef) {
            Long id = objRef.uniqueID();

            return new ReferenceGNode(objectMap.computeIfAbsent(id, key -> {
                if (objRef instanceof ArrayReference arrayRef) {
                    // Create a new ArrayNode, query JDI for its elements, and add to map
                    return createArrayNode(arrayRef);
                } else {
                    // Create a new ObjectNode, query JDI for its members, and add to map
                    return createObjectNode(objRef);
                }
            }));
        } else if (value instanceof PrimitiveValue primValue) {
            // create and return a PrimitiveNode from the value
            return createPrimitiveNode(primValue);
        }
        return null; // or some other kind of handling for unsupported types
    }

    private ObjectGNode createObjectNode(ObjectReference objRef) {
        ObjectGNode newNode = new ObjectGNode(objRef.uniqueID(), objRef.referenceType().name());
        for (Field field : objRef.referenceType().fields()) {
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
