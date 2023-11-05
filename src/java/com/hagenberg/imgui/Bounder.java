package com.hagenberg.imgui;

/**
 * A class that represents a bounding box in 2D space.
 */
public class Bounder {
  private Vec2 bound = new Vec2();
  private Vec2 offset = new Vec2();

  /**
   * Returns the current bound.
   * @return the current bound
   */
  public Vec2 getBound() {
    return bound;
  }

  public void addOffset(Vec2 offset) {
    this.offset.add(offset);
  }

  public void subtractOffset(Vec2 offset) {
    this.offset.subtract(offset);
  }
  
  /**
   * Adds the given value to the y-coordinate of the bound.
   * @param y the value to add to the y-coordinate
   */
  public void addY(float y) {
    bound.y += y;
  }
  
  /**
   * Adds the given value to the x-coordinate of the bound.
   * @param x the value to add to the x-coordinate
   */
  public void addX(float x) {
    bound.x += x;
  }

  /**
   * Updates the bound to include the given position.
   * @param pos the position to include in the bound
   */
  public void bound(Vec2 pos) {
    bound.x = Math.max(bound.x, offset.x + pos.x);
    bound.y = Math.max(bound.y, offset.y + pos.y);
  }
  
  /**
   * Updates the bound to include the given y-coordinate.
   * @param y the y-coordinate to include in the bound
   */
  public void boundY(float y) {
    bound.y = Math.max(bound.y, offset.y + y);
  }

  /**
   * Updates the bound to include the given x-coordinate.
   * @param x the x-coordinate to include in the bound
   */
  public void boundX(float x) {
    bound.x = Math.max(bound.x, offset.x + x);
  }
}
