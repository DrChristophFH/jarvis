package com.hagenberg.debuggee;

public class Robot {
  private int x;
  private int y;
  private int z;
  private final Display display = new Display();

  public String name = "Bernd";
  public boolean isAlive = true;

  public Robot() {
    this(0, 0, 0);
  }

  public Robot(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public void whereAreYou() {
    display.print("I am at (" + x + ", " + y + ", " + z + ")");
  }

  public void move(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
    whereAreYou();
  }

  public void howAreYou() {
    if (isAlive) {
      display.print("I am fine, thank you!");
    } else {
      display.print("I am dead!");
    }
  }

  @Override
  public String toString() {
    return "Robot{" +
        "x=" + x +
        ", y=" + y +
        ", z=" + z +
        ", name='" + name + '\'' +
        ", isAlive=" + isAlive +
        '}';
  }
}
