package com.hagenberg.debuggee;

import java.util.EmptyStackException;

public class UberRobot extends Robot {
  private String lel = "I am god";

  public String toString() {
    throw new EmptyStackException();
  }
}
