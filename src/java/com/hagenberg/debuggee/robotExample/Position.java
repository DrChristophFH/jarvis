package com.hagenberg.debuggee.robotExample;

public record Position(int x, int y, int z) {
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
