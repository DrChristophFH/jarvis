package com.hagenberg.jarvis.models;

import com.hagenberg.jarvis.config.AppConfig;
import com.hagenberg.jarvis.config.ConfigManager;
import com.hagenberg.jarvis.models.entities.wrappers.JArrayReference;
import com.hagenberg.jarvis.models.entities.wrappers.JArrayType;
import com.hagenberg.jarvis.models.entities.wrappers.JLocalVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JMember;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.models.entities.wrappers.JPrimitiveValue;
import com.hagenberg.jarvis.models.entities.wrappers.JReferenceType;
import com.hagenberg.jarvis.models.entities.wrappers.JType;
import com.hagenberg.jarvis.models.entities.wrappers.JValue;
import com.hagenberg.jarvis.models.entities.wrappers.JVariable;
import com.hagenberg.jarvis.models.entities.wrappers.ReferenceHolder;
import com.hagenberg.jarvis.util.Logger;
import com.hagenberg.jarvis.util.Observable;
import com.hagenberg.jarvis.util.Observer;
import com.hagenberg.jarvis.util.Pair;
import com.sun.jdi.*;

import imgui.type.ImString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.List;

public class ObjectGraphModel implements Observable {
  private final ClassModel classModel;

  // configuration
  private boolean resolveSpecialClasses = false;
  // classes that should not be resovled unless desired. This is necessary because
  // some classes blow up the object graph (e.g. reflection classes)
  private final List<ImString> specialClasses = new ArrayList<>();

  // locking
  private final ReentrantLock lock = new ReentrantLock();
  private final List<Observer> observers = new ArrayList<>();

  // model
  private final Map<LocalVariable, JLocalVariable> localVars = new HashMap<>(); // The roots are the local variables visible
  private final Map<LocalVariable, JLocalVariable> localVarBuffer = new HashMap<>(); // buffer for local variables
  private final Map<ObjectReference, JObjectReference> objectMap = new HashMap<>(); // maps object ids to graph objects
  private final Map<ObjectReference, JObjectReference> objectBuffer = new HashMap<>(); // buffer for objects

  private Consumer<Pair<JObjectReference, ObjectReference>> toStringDefer;
  private Logger logger = Logger.getInstance();

  public ObjectGraphModel(ClassModel classModel) {
    this.classModel = classModel;
    AppConfig config = ConfigManager.getInstance().getConfig();
    specialClasses.addAll(config.getSpecialClasses().stream().map(e -> new ImString(e, 255)).toList());
  }

  public void lockModel() {
    lock.lock();
  }

  public void unlockModel() {
    lock.unlock();
  }

  public void setResolveSpecialClasses(boolean resolveSpecialClasses) {
    this.resolveSpecialClasses = resolveSpecialClasses;
  }

  public boolean isResolveSpecialClasses() {
    return resolveSpecialClasses;
  }

  public List<ImString> getSpecialClasses() {
    return specialClasses;
  }

  public void saveSpecialClasses() {
    ConfigManager configManager = ConfigManager.getInstance();
    AppConfig config = configManager.getConfig();
    config.setSpecialClasses(specialClasses.stream().map(ImString::get).toList());
    configManager.saveConfig();
  }

  public void syncWith(ThreadReference currentThread, Consumer<Pair<JObjectReference, ObjectReference>> toStringDefer) {
    this.toStringDefer = toStringDefer;
    lockModel();
    try {
      List<StackFrame> frames = new ArrayList<>();

      try {
        frames = currentThread.frames();
      } catch (IncompatibleThreadStateException e) {
        logger.logError("IncompatibleThreadStateException while syncing with thread - Thread is not suspended");
      }

      localVarBuffer.clear();
      objectBuffer.clear();

      for (StackFrame frame : frames) {
        handleFrame(frame);
      }

      localVars.clear();
      objectMap.clear();
      localVars.putAll(localVarBuffer);
      objectMap.putAll(objectBuffer);

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

  public List<JLocalVariable> getJLocalVariables(List<LocalVariable> lvars) {
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

  private boolean isSpecialClass(String className) {
    for (ImString specialClass : specialClasses) {
      if (specialClass.get().equals(className)) {
        return true;
      }
    }
    return false;
  }

  private void handleFrame(StackFrame frame) {
    try {
      for (LocalVariable variable : frame.visibleVariables()) {
        handleLocalVariable(variable, frame);
      }
    } catch (AbsentInformationException e) {
      e.printStackTrace();
      logger.logError("AbsentInformationException while handling frame " + frame.location().toString());
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
      type = classModel.getJType(lvar.type());
    } catch (ClassNotLoadedException e) {
      e.printStackTrace();
      logger.logError("ClassNotLoadedException while creating local variable " + lvar.name());
    }

    JLocalVariable newVar = new JLocalVariable(lvar, type);
    return newVar;
  }

  private void updateValue(JLocalVariable localVariable, Value varValue) {
    if (varValue instanceof PrimitiveValue primValue) {
      localVariable.setValue(createJPrimitiveValue(primValue));
    } else {
      JObjectReference newValue = cycleObjectReference((ObjectReference) varValue);
      setReferences(localVariable, (JObjectReference) localVariable.value(), newValue); // cast must be safe
      localVariable.setValue(newValue);
    }
  }

  /**
   * Removes the reference holder from the old value and adds it to the new value.
   * 
   * @param refHolder the reference holder
   * @param oldVal    the old value
   * @param newVal    the new value
   */
  private void setReferences(ReferenceHolder refHolder, JObjectReference oldVal, JObjectReference newVal) {
    if (oldVal == newVal) return;

    // add variable as reference holder to object
    if (newVal != null) {
      newVal.addReferenceHolder(refHolder);
    }

    // remove variable as reference holder from old object
    if (oldVal != null) {
      oldVal.removeReferenceHolder(refHolder);
      if (oldVal.getReferenceHolders().isEmpty()) {
        removeObject(oldVal);
      }
    }
  }

  /**
   * Get the JObjectReference for the given ObjectReference by performing a full
   * "cycle". This means it is first checked if the object has already been cycled
   * and is present in the buffer. If not, it is checked if the object is in the
   * old object map. Objects retrieved from the old object map are cycled again to
   * update their values. If the object is not in the old object map, a new
   * JObjectReference is created.
   * 
   * @param objRef the ObjectReference to get the JObjectReference for
   * @return the JObjectReference for the given ObjectReference
   */
  private JObjectReference cycleObjectReference(ObjectReference objRef) {
    if (objRef == null) return null;

    JObjectReference existingObjRef = objectBuffer.get(objRef);

    if (existingObjRef == null) { // try to get non cycled object from old object map
      existingObjRef = objectMap.get(objRef);

      if (existingObjRef == null) { // object has never been seen before
        existingObjRef = createJObjectReference(objRef);
      }

      objectBuffer.put(objRef, existingObjRef); // add to buffer

      // cycle object to update values
      if (resolveSpecialClasses || !isSpecialClass(objRef.referenceType().name())) {
        updateMembers(existingObjRef);
        if (existingObjRef instanceof JArrayReference arrayRef) {
          updateContents(arrayRef);
        } else {
          // defer toString() resolution toString not defined for arrays
          deferToString(existingObjRef, objRef);
        }
      } else {
        existingObjRef.setToString("[not resolved]");
      }
    }
    return existingObjRef;
  }

  private void updateMembers(JObjectReference objRef) {
    for (JMember member : objRef.getMembers()) {
      Value varValue = objRef.getJdiObjectReference().getValue(member.field().getField());
      updateVariable(objRef, member, varValue);
    }
  }

  private void updateContents(JArrayReference arrayRef) {
    // update content list based on array reference since size can change
    List<Value> values = arrayRef.getJdiArrayReference().getValues();
    for (int i = 0; i < values.size(); i++) {
      Value value = values.get(i);
      updateVariable(arrayRef, arrayRef.getContent(i), value);
    }
  }

  private void updateVariable(JObjectReference obj, JVariable variable, Value varValue) {
    if (varValue instanceof PrimitiveValue primValue) {
      variable.setValue(createJPrimitiveValue(primValue));
    } else {
      JObjectReference oldValue = (JObjectReference) variable.value(); // cast must be safe
      JObjectReference newValue = cycleObjectReference((ObjectReference) varValue);
      setReferences(obj, oldValue, newValue);
      variable.setValue(newValue);
    }
  }

  /**
   * Creates a new JObjectReference for the given ObjectReference. The result is a
   * plain JObjectReference without any cycling.
   * 
   * @param objRef the ObjectReference to create a JObjectReference for
   * @return the created JObjectReference
   */
  private JObjectReference createJObjectReference(ObjectReference objRef) {
    JObjectReference newObjRef;

    if (objRef instanceof ArrayReference arrayRef) {
      newObjRef = createArrayNode(arrayRef);
    } else {
      newObjRef = createObjectNode(objRef);
    }

    return newObjRef;
  }

  private JObjectReference createObjectNode(ObjectReference objRef) {
    JReferenceType objType = (JReferenceType) classModel.getJType(objRef.referenceType());
    JObjectReference newNode = new JObjectReference(objRef, objType);
    return newNode;
  }

  private JObjectReference createArrayNode(ArrayReference arrayRef) {
    ArrayType jdiArrayType = (ArrayType) arrayRef.referenceType();
    JArrayType arrayType = (JArrayType) classModel.getJType(jdiArrayType);

    Type componentType;
    try {
      componentType = jdiArrayType.componentType();
    } catch (ClassNotLoadedException e) {
      componentType = null;
    }

    JArrayReference newNode = new JArrayReference(arrayRef, arrayType, classModel.getJType(componentType));

    return newNode;
  }

  private JValue createJPrimitiveValue(PrimitiveValue primValue) {
    return new JPrimitiveValue(primValue, classModel.getJType(primValue.type()));
  }

  private void removeObject(JObjectReference objectRef) {
    List<JObjectReference> removed = objectRef.removeAsReferenceHolder();
    objectMap.remove(objectRef.getJdiObjectReference());
    for (JObjectReference objRef : removed) {
      removeObject(objRef);
    }
  }

  private void deferToString(JObjectReference node, ObjectReference objRef) {
    this.toStringDefer.accept(new Pair<>(node, objRef));
  }
}
