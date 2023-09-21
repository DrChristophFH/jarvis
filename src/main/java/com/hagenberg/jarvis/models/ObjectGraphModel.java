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

    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"nodes\": [");
        for (LocalGVariable localVariable : nodes) {
            sb.append(variableToJSON(localVariable));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1); // remove the last comma
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    private String variableToJSON(GVariable variable) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"name\": \"");
        sb.append(variable.getName());
        sb.append("\",");
        sb.append("\"node\": ");
        sb.append(nodeToJSON(variable.getNode()));
        sb.append("}");
        return sb.toString();
    }

    private String nodeToJSON(GNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\": \"");
        sb.append(node.getType());
        sb.append("\",");
        sb.append("\"value\": ");

        if(node instanceof PrimitiveGNode) {
            sb.append(((PrimitiveGNode) node).getPrimitiveValue());
        } else if(node instanceof ReferenceGNode) {
            sb.append(nodeToJSON(((ReferenceGNode) node).getObject()));
        } else if(node instanceof ArrayGNode) {
            sb.append("[");
            for(GNode content : ((ArrayGNode) node).getContents()) {
                sb.append(nodeToJSON(content));
                sb.append(",");
            }
            if (!((ArrayGNode) node).getContents().isEmpty())
                sb.deleteCharAt(sb.length() - 1); // remove the last comma
            sb.append("]");
        } else if(node instanceof ObjectGNode) {
            sb.append("{");
            for(GVariable member : ((ObjectGNode) node).getMembers()) {
                sb.append("\"");
                sb.append(member.getName());
                sb.append("\": ");
                sb.append(nodeToJSON(member.getNode()));
                sb.append(",");
            }
            if (!((ObjectGNode) node).getMembers().isEmpty())
                sb.deleteCharAt(sb.length() - 1); // remove the last comma
            sb.append("}");
        }

        sb.append("}");
        return sb.toString();
    }
}
