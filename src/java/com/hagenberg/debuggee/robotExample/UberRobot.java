package com.hagenberg.debuggee.robotExample;

public class UberRobot extends Robot {
  public UberRobot(String identifier, Position position, Display display, Sensor sensor) {
    super(identifier, position, display, sensor);
  }
  
  public void move() {
    super.move();
    System.out.printf("Robot %s moves again%n", identifier);
    super.move();
  }
}
