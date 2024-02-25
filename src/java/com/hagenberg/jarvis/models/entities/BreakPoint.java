package com.hagenberg.jarvis.models.entities;

import java.beans.Transient;

import com.sun.jdi.request.BreakpointRequest;

public class BreakPoint {
  private String className = null;
  private int line = -1;
  private boolean enabled = true;
  private BreakpointRequest request = null;

  public BreakPoint() {
  }

  public BreakPoint(int line) {
    this.line = line;
  }

  public BreakPoint(String className, int line) {
    this.className = className;
    this.line = line;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public int getLine() {
    return line;
  }

  public void setLine(int line) {
    this.line = line;
  }

  @Transient // ignore this property when serializing
  public BreakpointRequest getRequest() {
    return request;
  }

  public void setRequest(BreakpointRequest request) {
    this.request = request;
  }

  public boolean isLive() {
    return request != null;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if (request != null) {
      request.setEnabled(enabled);
    }
  }
}
