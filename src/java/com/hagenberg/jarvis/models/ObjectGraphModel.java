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

  private final Map<LocalVariable, LocalGVariable> localVars = new HashMap<>(); // The roots are the local variables visible
  private final Map<Long, ObjectGNode> objectMap = new HashMap<>(); // maps object ids to graph objects

  private ThreadReference currentThread;

  public void syncWith(List<StackFrame> frames, ThreadReference currentThread) {
    this.currentThread = currentThread;
    List<LocalVariable> localVarsToRemove = new ArrayList<>(localVars.keySet());

    for (StackFrame frame : frames) {
      try {
        for (LocalVariable variable : frame.visibleVariables()) {
          Value varValue = frame.getValue(variable);
          StackFrameInformation sfInfo = new StackFrameInformation();
          if (localVars.containsKey(variable)) {
            updateVariable(localVars.get(variable), varValue);
          } else {
            addLocalVariable(variable, varValue, sfInfo);
          }
          localVarsToRemove.remove(variable);
        }
      } catch (AbsentInformationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    for (LocalVariable lvar : localVarsToRemove) {
      localVars.remove(lvar);
    }

    notifyObservers();
  }

  public List<ObjectGNode> getObjects() {
    synchronized (objectMap) {
      return new ArrayList<>(objectMap.values());
    }
  }

  public LocalGVariable getLocalVariable(LocalVariable lvar) {
    return localVars.get(lvar);
  }

  public List<LocalGVariable> getLocalVariables() {
    return new ArrayList<>(localVars.values());
  }

  public List<LocalGVariable> getLocalVariables(List<LocalVariable> lvars) {
    List<LocalGVariable> result = new ArrayList<>();
    for (LocalVariable lvar : lvars) {
      result.add(getLocalVariable(lvar));
    }
    return result;
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

  private void updateVariable(GVariable variable, Value varValue) {
    if (varValue instanceof ObjectReference objRef) {
      ObjectGNode newNode = objectMap.get(objRef.uniqueID());
      // new object in general
      if (newNode == null) {
        if (objRef instanceof ArrayReference arrayRef) {
          variable.setNode(createArrayNode(arrayRef));
        } else {
          variable.setNode(createObjectNode(objRef));
        }
      } else { // existing object
        ObjectGNode currentNode = (ObjectGNode) variable.getNode();
        currentNode.removeReferenceHolder(variable);
        newNode.addReferenceHolder(variable);
        variable.setNode(newNode);

        // update members
        updateMembers(newNode, objRef);

        if (newNode instanceof ArrayGNode newArrayNode) {
          updateContents(newArrayNode, (ArrayReference) objRef);
        }

        if (currentNode.getReferenceHolders().isEmpty()) {
          objectMap.remove(currentNode.getObjectId());
        }
      }
    } else if (varValue instanceof PrimitiveValue primValue) {
      variable.setNode(createPrimitiveNode(primValue));
    }
  }

  private void updateMembers(ObjectGNode node, ObjectReference objRef) {
    for (MemberGVariable member : node.getMembers()) {
      updateVariable(member, objRef.getValue(member.getField()));
    }
  }

  private void updateContents(ArrayGNode node, ArrayReference arrayRef) {
    for (ContentGVariable member : node.getContentGVariables()) {
      updateVariable(member, arrayRef.getValue(member.getIndex()));
    }
  }

  private void addLocalVariable(LocalVariable lvar, Value varValue, StackFrameInformation sfInfo) {

    Type staticType = null;

     try {
      staticType = lvar.type();
    } catch (ClassNotLoadedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    LocalGVariable newVar = new LocalGVariable(lvar.name(), staticType, sfInfo);

    if (varValue instanceof ObjectReference objRef) {
      newVar.setNode(lookUpObjectNode(objRef, newVar));
    } else if (varValue instanceof PrimitiveValue primValue) {
      newVar.setNode(createPrimitiveNode(primValue));
    }

    synchronized (localVars) {
      localVars.put(lvar, newVar);
    }
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
    ObjectGNode newNode = new ObjectGNode(objRef.uniqueID(), objRef.referenceType());
    newNode.setToString(resolveToString(objRef));
    for (Field field : objRef.referenceType().fields()) {
      if (field.isStatic()) continue; // skip static fields

      Value fieldValue = objRef.getValue(field);
      Type fieldType = null;
      try {
        fieldType = field.type();
      } catch (ClassNotLoadedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      MemberGVariable member = createMemberGVariable(field, newNode, field.name(), fieldType, fieldValue, field.modifiers());
      newNode.addMember(member);
    }
    return newNode;
  }

  private String resolveToString(ObjectReference objRef) {
    String result;

    try {
      result = objRef.invokeMethod(currentThread, objRef.referenceType().methodsByName("toString").get(0), new ArrayList<>(), ObjectReference.INVOKE_SINGLE_THREADED).toString();
    } catch (InvalidTypeException | ClassNotLoadedException | IncompatibleThreadStateException | InvocationException e) {
      result = "toString() not available";
    }

    return result;
  }

  private ObjectGNode createArrayNode(ArrayReference arrayRef) {
    ArrayGNode newNode = new ArrayGNode(arrayRef.uniqueID(), arrayRef.referenceType());
    List<Value> values = arrayRef.getValues();
    for (int i = 0; i < values.size(); i++) {
      Value value = values.get(i);
      ContentGVariable arrayMember = createContentGVariable(newNode, "[" + i + "]", newNode.getType(), value, i);
      newNode.addContent(arrayMember);
    }
    return newNode;
  }

  private GNode createPrimitiveNode(PrimitiveValue primValue) {
    return new PrimitiveGNode(primValue.type(), primValue.toString());
  }

  private MemberGVariable createMemberGVariable(Field field, ObjectGNode parent, String name, Type staticType, Value value, int accessModifier) {
    MemberGVariable member = new MemberGVariable(field, name, staticType, parent, accessModifier);

    if (value instanceof ObjectReference objRef) {
      member.setNode(lookUpObjectNode(objRef, member));
    } else if (value instanceof PrimitiveValue primValue) {
      member.setNode(createPrimitiveNode(primValue));
    }

    return member;
  }

  private ContentGVariable createContentGVariable(ObjectGNode parent, String name, Type staticType, Value value, int index) {
    ContentGVariable member = new ContentGVariable(name, staticType, parent, index);

    if (value instanceof ObjectReference objRef) {
      member.setNode(lookUpObjectNode(objRef, member));
    } else if (value instanceof PrimitiveValue primValue) {
      member.setNode(createPrimitiveNode(primValue));
    }

    return member;
  }
}
