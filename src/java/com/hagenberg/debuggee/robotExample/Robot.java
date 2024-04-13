package com.hagenberg.debuggee.robotExample;

public class Robot {
  protected final String identifier;
  private Position position;
  private Display display;
  private Sensor sensor;
  
  public Robot(String identifier, Position position, Display display, Sensor sensor) {
    this.identifier = identifier;
    this.position = position;
    this.display = display;
    this.sensor = sensor;
    System.out.printf("Robot %s created%n", identifier);
  }

  public void move() {
    Robot nearestRobot = sensor.getNearestRobot(position, this);
    if (nearestRobot != null) {
      System.out.printf("Robot %s is near robot %s%n", identifier, nearestRobot.identifier);
      if (nearestRobot.position.isNear(position)) {
        System.out.printf("Robot %s leaves%n", identifier);
        position = position.plus(Position.random().times((int)(Math.random() * 3)));
        display.display("Goodbye " + nearestRobot.identifier);
      } else {
        System.out.printf("Robot %s moves towards robot %s%n", identifier, nearestRobot.identifier);
        position = position.moveTo(nearestRobot.position);
        display.display("Hello " + nearestRobot.identifier);
      }
    }
  }

  public void communicate() {
    Robot nearestRobot = sensor.getNearestRobot(position, this);
    if (nearestRobot != null && nearestRobot.position.isNear(position)) {
      System.out.printf("Robot %s reads: \"%s\"%n", identifier, nearestRobot.display.read());
    } else {
      System.out.printf("Robot %s is alone%n", identifier);
      display.display("Hmmm...");
    }
  }

  public Position getPosition() {
    return position;
  }
}
