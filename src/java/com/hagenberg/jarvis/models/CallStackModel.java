package com.hagenberg.jarvis.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.hagenberg.jarvis.models.entities.CallStackFrame;
import com.hagenberg.jarvis.models.entities.wrappers.JMethod;
import com.hagenberg.jarvis.models.entities.wrappers.JType;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;

public class CallStackModel {

  private final ObjectGraphModel objectGraphModel;
  private final ClassModel classModel;
  private final List<CallStackFrame> callStack = new CopyOnWriteArrayList<>();

  public CallStackModel(ObjectGraphModel objectGraphModel, ClassModel classModel) {
    this.objectGraphModel = objectGraphModel;
    this.classModel = classModel;
  }

  /**
   * Builds the call stack from the given frames. Must be rebuild every step
   * due to lifetime of StackFrames from JDI.
   * Must be called after LocalVariables have been added to the ObjectGraphModel.
   * @param frames
   */
  public void syncWith(List<StackFrame> frames) {
    callStack.clear();
    Collections.reverse(frames); // jdi has most recent frame at index 0

    for (StackFrame frame : frames) {
      List<LocalVariable> parameters = new ArrayList<>();

      try {
        for (LocalVariable variable : frame.visibleVariables()) {
          if (variable.isArgument()) {
            parameters.add(variable);
          }
        }
      } catch (AbsentInformationException e) {
        e.printStackTrace();
      }
      
      JType classType = classModel.getJType(frame.location().declaringType());
      JMethod method = classModel.getJMethodInType(classType, frame.location().method());

      CallStackFrame newFrame = new CallStackFrame(
        frame,
        classType,
        method,
        objectGraphModel.getJLocalVariables(parameters),
        frame.location().lineNumber()
      );
      
      callStack.add(newFrame);
    }
  }

  public List<CallStackFrame> getCallStack() {
    return callStack;
  }

  public void clear() {
    callStack.clear();
  }
}
