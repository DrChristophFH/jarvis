package com.hagenberg.debuggee.robotExampleSingle;

import java.util.ArrayList;
import java.util.List;

public class RobotTest {

  public static class Robot {
    protected final String identifier;
    private Position position;
    private Display display;
    private Sensor sensor;

    public String name = "Bernd";
    public boolean isAlive = true;

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
          position = position.plus(Position.random().times((int) (Math.random() * 3)));
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

  public static class Display {
    private String displayedText = "";

    public void display(String text) {
      displayedText = text;
    }

    public String read() {
      return displayedText;
    }
  }

  public static record Position(int x, int y, int z) {
    public Position plus(Position other) {
      return new Position(x + other.x, y + other.y, z + other.z);
    }

    public Position minus(Position other) {
      return new Position(x - other.x, y - other.y, z - other.z);
    }

    public float distanceTo(Position other) {
      return (float) Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2));
    }

    public Position moveTo(Position position) {
      return new Position(position.x, position.y, position.z);
    }

    public boolean isNear(Position position) {
      return distanceTo(position) < 2;
    }

    public static Position random() {
      return new Position((int) (Math.random() * 3) - 1, (int) (Math.random() * 3) - 1, (int) (Math.random() * 3) - 1);
    }

    public Position times(int factor) {
      return new Position(x * factor, y * factor, z * factor);
    }
  }

  public static class Sensor {
    private WorldQuery world;

    public Sensor(WorldQuery world) {
      this.world = world;
    }

    public Robot getNearestRobot(Position position, Robot self) {
      return world.getNearestRobot(position, self);
    }
  }

  public static interface WorldQuery {
    public Robot getNearestRobot(Position position, Robot self);
  }

  public static class UberRobot extends Robot {
    public UberRobot(String identifier, Position position, Display display, Sensor sensor) {
      super(identifier, position, display, sensor);
    }
    
    public void move() {
      super.move();
      System.out.printf("Robot %s moves again%n", identifier);
      super.move();
    }
  }

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