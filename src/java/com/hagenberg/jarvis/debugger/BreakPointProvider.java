package com.hagenberg.jarvis.debugger;

import java.util.List;

import com.hagenberg.jarvis.models.entities.BreakPoint;

public interface BreakPointProvider {
  public List<BreakPoint> getBreakPoints(String className);
  public void setBreakPointCreationCallback(BreakPointCreationCallback callback);

  public interface BreakPointCreationCallback {
    public void register(BreakPoint breakPoint);
  }
}
