package com.hagenberg.debuggee.robotExample;

@FunctionalInterface
public interface WorldQuery {
  public Robot getNearestRobot(Position position, Robot self);
}
