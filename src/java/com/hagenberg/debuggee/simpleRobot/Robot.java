package com.hagenberg.debuggee.simpleRobot;

public class Robot {
  private int x;
  private int y;

  public void move(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
}
