package com.hagenberg.debuggee.simpleRobot;

public class Main {
  public static void main(String[] args) {
    Robot robot = new Robot();
    Robot robot2 = new Robot();
    Robot robot3 = robot;

    robot.move(10, 20);
    robot.move(30, 40);
    robot.move(50, 60);

    robot2.move(100, 200);

  }
}
