package com.hagenberg.debuggee.robotExample;

import java.util.ArrayList;
import java.util.List;

public class RobotTest {
  public static void main(String[] args) {
    List<Robot> robots = new ArrayList<>();

    WorldQuery wq = (position, self) -> {
      Robot nearestRobot = null;
      double minDistance = Double.MAX_VALUE;
      for (Robot robot : robots) {
        if (robot == self) {
          continue;
        }
        double distance = robot.getPosition().distanceTo(position);
        if (distance < minDistance) {
          minDistance = distance;
          nearestRobot = robot;
        }
      }
      return nearestRobot;
    };

    Robot robot1 = new Robot("R2D2", new Position(0, 0, 0), new Display(), new Sensor(wq));
    Robot robot2 = new Robot("C3PO", new Position(3, 4, 0), new Display(), new Sensor(wq));
    UberRobot uberRobot = new UberRobot("BB8", new Position(5, 5, 5), new Display(), new Sensor(wq));

    robots.add(robot1);
    robots.add(robot2);
    robots.add(uberRobot);

    for (int i = 0; i < 10; i++) {
      for (Robot robot : robots) {
        robot.move();
        robot.communicate();
      }
    }
  }
}
