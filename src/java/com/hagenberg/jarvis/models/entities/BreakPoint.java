package com.hagenberg.jarvis.models.entities;

public class BreakPoint {
  private int line;
  private boolean enabled;

  public BreakPoint() {
    this.line = 0;
    this.enabled = true;
  }

  public BreakPoint(int line) {
    this.line = line;
    this.enabled = true;
  }

  public int getLine() {
    return line;
  }

  public void setLine(int line) {
    this.line = line;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
