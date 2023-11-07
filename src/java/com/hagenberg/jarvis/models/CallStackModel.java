package com.hagenberg.jarvis.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.hagenberg.jarvis.models.entities.CallStackFrame;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;

public class CallStackModel {
  private List<CallStackFrame> callStack = new CopyOnWriteArrayList<>();

  /**
   * Builds the call stack from the given frames. Must be rebuild every step
   * due to lifetime of StackFrames from JDI.
   * Must be called after LocalVariables have been added to the ObjectGraphModel.
   * @param frames
   */
  public void syncWith(List<StackFrame> frames, ObjectGraphModel objectGraphModel) {
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
      
      CallStackFrame newFrame = new CallStackFrame(
        frame,
        frame.location().declaringType(),
        frame.location().method(),
        objectGraphModel.getLocalVariables(parameters),
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
