package com.hagenberg.jarvis.models;

import com.hagenberg.jarvis.models.entities.graph.*;
import com.hagenberg.jarvis.util.Observable;
import com.hagenberg.jarvis.util.Observer;
import com.hagenberg.jarvis.util.Pair;
import com.sun.jdi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;

public class ObjectGraphModel implements Observable {
  private final ReentrantLock lock = new ReentrantLock();
  private final List<Observer> observers = new ArrayList<>();

  private final Map<LocalVariable, LocalGVariable> localVars = new HashMap<>(); // The roots are the local variables visible
  private final Map<Long, ObjectGNode> objectMap = new HashMap<>(); // maps object ids to graph objects

  private final List<Pair<ObjectGNode, ObjectReference>> deferredToString = new ArrayList<>();

  private ThreadReference currentThread;

  public void lockModel() {
    lock.lock();
  }

  public void unlockModel() {
    lock.unlock();
  }

  public void syncWith(ThreadReference currentThread) {
    lockModel();
    try {
      this.currentThread = currentThread;
      List<StackFrame> frames = new ArrayList<>();
      try {
        frames = currentThread.frames();
      } catch (IncompatibleThreadStateException e) {
        System.out.println("Thread is not suspended");
      }
      List<LocalVariable> localVarsToRemove = new ArrayList<>(localVars.keySet());

      for (StackFrame frame : frames) {
        try {
          for (LocalVariable variable : frame.visibleVariables()) {
            Value varValue = frame.getValue(variable);
            if (localVars.containsKey(variable)) {
              updateVariable(localVars.get(variable), varValue);
            } else {
              StackFrameInformation sfInfo = new StackFrameInformation();
              addLocalVariable(variable, varValue, sfInfo);
            }
            localVarsToRemove.remove(variable);
          }
        } catch (AbsentInformationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      resolveToString();

      for (LocalVariable lvar : localVarsToRemove) {
        localVars.remove(lvar);
      }

      notifyObservers();
    } finally {
      unlockModel();
    }
  }

  public List<ObjectGNode> getObjects() {
    return new ArrayList<>(objectMap.values());
  }

  public LocalGVariable getLocalVariable(LocalVariable lvar) {
    return localVars.get(lvar);
  }

  public List<LocalGVariable> getLocalVariables() {
    return new ArrayList<>(localVars.values());
  }

  public List<LocalGVariable> getLocalVariables(List<LocalVariable> lvars) {
    List<LocalGVariable> result = new ArrayList<>();
    lockModel();
    try {
      for (LocalVariable lvar : lvars) {
        result.add(getLocalVariable(lvar));
      }
    } finally {
      unlockModel();
    }
    return result;
  }

  public void clear() {
    lockModel();
    try {
      localVars.clear();
      objectMap.clear();
      notifyObservers();
    } finally {
      unlockModel();
    }
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
      ObjectGNode existingNode = objectMap.get(objRef.uniqueID());

      if (existingNode == null) { // object has no corresponding node yet
        // create new node
        if (objRef instanceof ArrayReference arrayRef) {
          existingNode = createArrayNode(arrayRef);
        } else {
          existingNode = createObjectNode(objRef);
        }
        // put node into map to track it
        objectMap.put(objRef.uniqueID(), existingNode);
      } else { // object already has a corresponding node
        // update members
        updateMembers(existingNode, objRef);

        // update contents
        if (existingNode instanceof ArrayGNode newArrayNode) {
          updateContents(newArrayNode, (ArrayReference) objRef);
        }
      }
      updateHeldNode(variable, existingNode);
    } else if (varValue instanceof PrimitiveValue primValue) {
      variable.setNode(createPrimitiveNode(primValue));
    } else {
      updateHeldNode(variable, null);
    }
  }

  /**
   * Update the held node of the variable. Adjusts the reference holders of the old and new node.
   * 
   * @param variable
   * @param newNode
   */
  private void updateHeldNode(GVariable variable, ObjectGNode newNode) {
    ObjectGNode lastHeldNode = null;

    try {
      lastHeldNode = (ObjectGNode) variable.getNode();
    } catch (ClassCastException e) {
      throw new NodeAssignmentException("Tried assigning an Object Node to a primitive holding variable.", e);
    }

    if (lastHeldNode != newNode) {
      setNewNode(variable, newNode);
      removeReference(variable, lastHeldNode);
    }
  }

  /**
   * Remove the variable from the reference holders of the object node and remove the object node if it has no more reference
   * holders.
   * 
   * @param variable the variable to remove as reference holder
   * @param objNode  the object node to check for references
   */
  private void removeReference(GVariable variable, ObjectGNode objNode) {
    if (objNode != null) {
      objNode.removeReferenceHolder(variable);
      if (objNode.getReferenceHolders().isEmpty()) {
        removeObject(objNode);
      }
    }
  }

  /**
   * Set the new node for the variable and add the variable as reference holder to the new node.
   * 
   * @param variable the variable to set the new node for
   * @param newNode  the new node to set
   */
  private void setNewNode(GVariable variable, ObjectGNode newNode) {
    if (newNode != null) {
      newNode.addReferenceHolder(variable);
    }
    variable.setNode(newNode);
  }

  private void removeObject(ObjectGNode currentNode) {
    if (currentNode instanceof ArrayGNode arrayNode) {
      for (ContentGVariable content : arrayNode.getContent()) {
        if (content.getNode() instanceof ObjectGNode contentNode) {
          contentNode.removeReferenceHolder(content);
          if (contentNode.getReferenceHolders().isEmpty()) {
            removeObject(contentNode);
          }
        }
      }
    }
    for (MemberGVariable member : currentNode.getMembers()) {
      if (member.getNode() instanceof ObjectGNode memberNode) {
        memberNode.removeReferenceHolder(member);
        if (memberNode.getReferenceHolders().isEmpty()) {
          removeObject(memberNode);
        }
      }
    }
    objectMap.remove(currentNode.getObjectId());
  }

  private void updateMembers(ObjectGNode node, ObjectReference objRef) {
    for (MemberGVariable member : node.getMembers()) {
      updateVariable(member, objRef.getValue(member.getField()));
    }
  }

  private void updateContents(ArrayGNode node, ArrayReference arrayRef) {
    // update content list based on array reference since size can change
    List<Value> values = arrayRef.getValues();
    for (int i = 0; i < values.size(); i++) {
      Value value = values.get(i);
      ContentGVariable arrayMember = node.getContent().get(i);
      updateVariable(arrayMember, value);
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

    LocalGVariable newVar = new LocalGVariable(lvar.name(), staticType, lvar, sfInfo);

    if (varValue instanceof ObjectReference objRef) {
      newVar.setNode(lookUpObjectNode(objRef, newVar));
    } else if (varValue instanceof PrimitiveValue primValue) {
      newVar.setNode(createPrimitiveNode(primValue));
    }

    localVars.put(lvar, newVar);
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
    deferToString(newNode, objRef);
    for (Field field : objRef.referenceType().allFields()) {
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

  private void deferToString(ObjectGNode node, ObjectReference objRef) {
    deferredToString.add(new Pair<>(node, objRef));
  }

  /**
   * <b>NOTE:</b> This method will <b>RESUME</b> the current thread, therefore invalidating all stack frames!
   */
  private void resolveToString() {
    String result;
    for (Pair<ObjectGNode, ObjectReference> pair : deferredToString) {
      ObjectGNode node = pair.first();
      ObjectReference objRef = pair.second();
      try {
        List<Method> methods = objRef.referenceType().methodsByName("toString", "()Ljava/lang/String;");
        if (methods.isEmpty()) {
          result = "toString() not available";
        } else {
          Method toStringMethod = methods.get(0);
          int flags = ObjectReference.INVOKE_SINGLE_THREADED;
          result = objRef.invokeMethod(currentThread, toStringMethod, new ArrayList<>(), flags).toString();
        }
      } catch (IllegalArgumentException | InvalidTypeException | ClassNotLoadedException | IncompatibleThreadStateException | InvocationException e) {
        result = "toString() not available";
      }
      node.setToString(result);
    }
    deferredToString.clear();
  }

  private ObjectGNode createArrayNode(ArrayReference arrayRef) {
    ArrayGNode newNode = new ArrayGNode(arrayRef.uniqueID(), arrayRef.referenceType());
    Type componentType;
    try {
      componentType = ((ArrayType) arrayRef.referenceType()).componentType();
    } catch (ClassNotLoadedException e) {
      componentType = null;
    }
    List<Value> values = arrayRef.getValues();
    for (int i = 0; i < values.size(); i++) {
      Value value = values.get(i);
      ContentGVariable arrayMember = createContentGVariable(newNode, "[" + i + "]", componentType, value, i);
      newNode.addContent(arrayMember);
    }
    return newNode;
  }

  private GNode createPrimitiveNode(PrimitiveValue primValue) {
    return new PrimitiveGNode(primValue.type(), primValue);
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
