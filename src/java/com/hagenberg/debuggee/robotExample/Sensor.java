package com.hagenberg.debuggee.robotExample;

public class Sensor {
  private WorldQuery world;

  public Sensor(WorldQuery world) {
    this.world = world;
  }

  public Robot getNearestRobot(Position position, Robot self) {
    return world.getNearestRobot(position, self);
  }
}
