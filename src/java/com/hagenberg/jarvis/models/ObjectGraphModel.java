package com.hagenberg.jarvis.models;

import com.hagenberg.jarvis.models.entities.graph.*;
import com.hagenberg.jarvis.models.entities.wrappers.JArrayReference;
import com.hagenberg.jarvis.models.entities.wrappers.JLocalVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.models.entities.wrappers.JPrimitiveType;
import com.hagenberg.jarvis.models.entities.wrappers.JPrimitiveValue;
import com.hagenberg.jarvis.models.entities.wrappers.JType;
import com.hagenberg.jarvis.models.entities.wrappers.JValue;
import com.hagenberg.jarvis.models.entities.wrappers.ReferenceHolder;
import com.hagenberg.jarvis.util.Observable;
import com.hagenberg.jarvis.util.Observer;
import com.hagenberg.jarvis.util.Pair;
import com.sun.jdi.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;

public class ObjectGraphModel implements Observable {
  private final ClassModel classModel;

  // locking
  private final ReentrantLock lock = new ReentrantLock();
  private final List<Observer> observers = new ArrayList<>();

  // model
  private final Map<LocalVariable, JLocalVariable> localVars = new HashMap<>(); // The roots are the local variables visible
  private final Map<LocalVariable, JLocalVariable> localVarBuffer = new HashMap<>(); // buffer for local variables
  private final Map<ObjectReference, JObjectReference> objectMap = new HashMap<>(); // maps object ids to graph objects

  // deferred toString() resolution
  private final List<Pair<JObjectReference, ObjectReference>> deferredToString = new ArrayList<>();
  private ThreadReference currentThread; // current thread for toString() resolution

  public ObjectGraphModel(ClassModel classModel) {
    this.classModel = classModel;
  }

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

      localVarBuffer.clear();

      for (StackFrame frame : frames) {
        handleFrame(frame);
      }

      resolveToString();

      localVars.clear();
      localVars.putAll(localVarBuffer);

      notifyObservers();
    } finally {
      unlockModel();
    }
  }

  public List<JObjectReference> getObjects() {
    List<JObjectReference> result = new ArrayList<>();
    lockModel();
    try {
      result.addAll(objectMap.values());
    } finally {
      unlockModel();
    }
    return result;
  }

  public JLocalVariable getLocalVariable(LocalVariable lvar) {
    return localVars.get(lvar);
  }

  public List<JLocalVariable> getLocalVariables() {
    return new ArrayList<>(localVars.values());
  }

  public List<JLocalVariable> getLocalVariables(List<LocalVariable> lvars) {
    List<JLocalVariable> result = new ArrayList<>();
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

  private void handleFrame(StackFrame frame) {
    try {
      for (LocalVariable variable : frame.visibleVariables()) {
        handleLocalVariable(variable, frame);
      }
    } catch (AbsentInformationException e) {
      e.printStackTrace(); // TODO Auto-generated catch block
    }
  }

  private void handleLocalVariable(LocalVariable variable, StackFrame frame) {
    Value newValue = frame.getValue(variable);
    JLocalVariable localVar = localVars.get(variable);

    if (localVar == null) {
      localVar = createJLocalVariable(variable);
    }

    updateValue(localVar, newValue);

    localVarBuffer.put(variable, localVar);
  }

  private JLocalVariable createJLocalVariable(LocalVariable lvar) {
    JType type = null;

    try {
      type = getType(lvar.type());
    } catch (ClassNotLoadedException e) {
      e.printStackTrace(); // TODO due to lvar.type()
    }

    JLocalVariable newVar = new JLocalVariable(lvar, type);
    return newVar;
  }

  private void updateValue(JLocalVariable localVariable, Value varValue) {
    if (varValue instanceof ObjectReference objRef) {
      JObjectReference existingObjRef = getJObjectReference(objRef);
      handleObjectReferences(localVariable, existingObjRef);
    } else if (varValue instanceof PrimitiveValue primValue) {
      localVariable.setValue(createPrimitiveNode(primValue));
    } else { // null value
      handleObjectReferences(localVariable, null);
    }
  }

  private void handleObjectReferences(JLocalVariable variable, JObjectReference newNode) {
    JObjectReference lastValue = null;

    try {
      lastValue = (JObjectReference) variable.value();
    } catch (ClassCastException e) {
      throw new NodeAssignmentException("Tried assigning an Object Node to a primitive holding variable.", e);
    }

    if (lastValue != newNode) {
      // add variable as reference holder to object
      if (newNode != null) {
        newNode.addReferenceHolder(variable);
      }
      variable.setValue(newNode);

      // remove variable as reference holder from old object
      if (lastValue != null) {
        lastValue.removeReferenceHolder(variable);
        if (lastValue.getReferenceHolders().isEmpty()) {
          removeObject(lastValue);
        }
      }
    }
  }

  /**
   * Get the JObjectReference for the given ObjectReference. If the object has
   * never been seen before, a new JObjectReference is created and returned.
   * 
   * @param objRef the ObjectReference to get the JObjectReference for
   * @return the JObjectReference for the given ObjectReference
   */
  private JObjectReference getJObjectReference(ObjectReference objRef) {
    JObjectReference existingObjRef = objectMap.get(objRef);

    if (existingObjRef == null) { // object has never been seen before
      existingObjRef = createJObjectReference(objRef);
    } else { // object already exists
      existingObjRef.refresh();
    }

    return existingObjRef;
  }

  private JObjectReference createJObjectReference(ObjectReference objRef) {
    JObjectReference existingObjRef;

    if (objRef instanceof ArrayReference arrayRef) {
      existingObjRef = createArrayNode(arrayRef);
    } else {
      existingObjRef = createObjectNode(objRef);
    }

    // put node into map to track it
    objectMap.put(objRef, existingObjRef);
    return existingObjRef;
  }

  private void removeObject(JObjectReference objectRef) {
    removeReferenceHolder(objectRef, objectRef.getValues());
    objectMap.remove(objectRef.getObjectReference());
  }

  private void removeReferenceHolder(ReferenceHolder referenceHolder, Collection<JValue> values) {
    for (JValue value : values) {
      if (value instanceof JObjectReference objRef) {
        objRef.removeReferenceHolder(referenceHolder);
        if (objRef.getReferenceHolders().isEmpty()) {
          removeObject(objRef);
        }
      }
    }
  }

  private void updateMembers(JObjectReference node, ObjectReference objRef) {
    for (MemberGVariable member : node.getMembers()) {
      handleObjectReferences(member, objRef.getValue(member.getField()));
    }
  }

  private void updateContents(JArrayReference node, ArrayReference arrayRef) {
    // update content list based on array reference since size can change
    List<Value> values = arrayRef.getValues();
    for (int i = 0; i < values.size(); i++) {
      Value value = values.get(i);
      ContentGVariable arrayMember = node.getValues().get(i);
      handleObjectReferences(arrayMember, value);
    }
  }

  private JObjectReference createObjectNode(ObjectReference objRef) {
    JObjectReference newNode = new JObjectReference(objRef.uniqueID(), objRef.referenceType());
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

  private void deferToString(JObjectReference node, ObjectReference objRef) {
    deferredToString.add(new Pair<>(node, objRef));
  }

  /**
   * <b>NOTE:</b> This method will <b>RESUME</b> the current thread, therefore
   * invalidating all stack frames!
   */
  private void resolveToString() {
    String result = null;
    for (Pair<JObjectReference, ObjectReference> pair : deferredToString) {
      JObjectReference node = pair.first();
      ObjectReference objRef = pair.second();
      try {
        List<Method> methods = objRef.referenceType().methodsByName("toString", "()Ljava/lang/String;");
        if (!methods.isEmpty()) {
          Method toStringMethod = methods.get(0);
          // skip base object toString() method
          if (!toStringMethod.declaringType().name().equals("java.lang.Object")) {
            int flags = ObjectReference.INVOKE_SINGLE_THREADED;
            result = objRef.invokeMethod(currentThread, toStringMethod, new ArrayList<>(), flags).toString();
          }
        }
      } catch (IllegalArgumentException | InvalidTypeException | ClassNotLoadedException | IncompatibleThreadStateException
          | InvocationException e) {
        result = "toString() not available";
      }
      node.setToString(result);
    }
    deferredToString.clear();
  }

  private JObjectReference createArrayNode(ArrayReference arrayRef) {
    JArrayReference newNode = new JArrayReference(arrayRef.uniqueID(), arrayRef.referenceType());
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

  private JValue createPrimitiveNode(PrimitiveValue primValue) {
    return new JPrimitiveValue(primValue.type(), primValue);
  }

  private JType getType(Type type) {
    if (type instanceof PrimitiveType primType) {
      return new JPrimitiveType(primType);
    } else if (type instanceof ClassType classType) {
      return classModel.getOrCreate(classType);
    } else if (type instanceof ArrayType arrayType) {
      return classModel.getOrCreate(arrayType);
    } else if (type instanceof InterfaceType ifaceType) {
      return classModel.getOrCreate(ifaceType);
    } else {
      return null;
    }
  }
}
