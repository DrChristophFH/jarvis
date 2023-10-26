package com.hagenberg.imgui;

import imgui.ImDrawList;

public class Draw {
  private final ImDrawList dList;
  private Vec2 offset;

  public Draw(ImDrawList dList) {
    this(dList, new Vec2(0, 0));
  }

  public Draw(ImDrawList dList, Vec2 offset) {
    this.dList = dList;
    this.offset = offset;
  }

  public void setOffset(Vec2 offset) {
    this.offset = offset;
  }

  /**
   * Pushes a new clip rectangle onto the stack.
   *
   * @param p0 the top-left corner of the rectangle
   * @param p1 the bottom-right corner of the rectangle
   * @param intersectWithCurrentClipRect if true, the new clip rectangle will be intersected with the current clip rectangle
   */
  public void pushClipRect(Vec2 p0, Vec2 p1, boolean intersectWithCurrentClipRect) {
    dList.pushClipRect(p0.x + offset.x, p0.y + offset.y, p1.x + offset.x, p1.y + offset.y, intersectWithCurrentClipRect);
  }

  /**
   * Pops the last clip rectangle from the stack.
   */
  public void popClipRect() {
    dList.popClipRect();
  }

  /**
   * Adds a filled rectangle to the drawing list.
   *
   * @param p0 the top-left corner of the rectangle
   * @param p1 the bottom-right corner of the rectangle
   * @param color the color of the rectangle as ImGuiCol
   */
  public void addRectFilled(Vec2 p0, Vec2 p1, int color) {
    dList.addRectFilled(p0.x + offset.x, p0.y + offset.y, p1.x + offset.x, p1.y + offset.y, color);
  }


  /**
   * Adds a rectangle to the drawing list.
   *
   * @param p0 the top-left corner of the rectangle
   * @param p1 the bottom-right corner of the rectangle
   * @param color the color of the rectangle as ImGuiCol
   */
  public void addRect(Vec2 p0, Vec2 p1, int color) {
    dList.addRect(p0.x + offset.x, p0.y + offset.y, p1.x + offset.x, p1.y + offset.y, color);
  }

  /**
   * Adds a rectangle to the drawing list with a specified line width.
   *
   * @param p0 the top-left corner of the rectangle
   * @param p1 the bottom-right corner of the rectangle
   * @param color the color of the rectangle as an integer
   * @param lineWidth the width of the rectangle's line
   */
  public void addRect(Vec2 p0, Vec2 p1, int color, float lineWidth) {
    dList.addRect(p0.x + offset.x, p0.y + offset.y, p1.x + offset.x, p1.y + offset.y, color, 0, 0, lineWidth);
  }


  /**
   * Adds a filled circle to the drawing list.
   *
   * @param origin the center of the circle
   * @param radius the radius of the circle
   * @param color the color of the circle as ImGuiCol
   * @param segments the number of segments used to draw the circle
   */
  public void addCircleFilled(Vec2 origin, float radius, int color, int segments) {
    dList.addCircleFilled(origin.x + offset.x, origin.y + offset.y, radius, color, segments);
  }

  /**
   * Adds a circle to the drawing list.
   *
   * @param origin the center of the circle
   * @param radius the radius of the circle
   * @param color the color of the circle as ImGuiCol
   * @param segments the number of segments used to draw the circle
   */
  public void addCircle(Vec2 origin, float radius, int color, int segments) {
    dList.addCircle(origin.x + offset.x, origin.y + offset.y, radius, color, segments);
  }

  /**
   * Adds a line to the drawing list.
   *
   * @param x0 the x-coordinate of the start point of the line
   * @param y0 the y-coordinate of the start point of the line
   * @param x1 the x-coordinate of the end point of the line
   * @param y1 the y-coordinate of the end point of the line
   * @param color the color of the line as ImGuiCol
   */
  public void addLine(Vec2 p1, Vec2 p2, int color, float thickness) {
    dList.addLine(p1.x + offset.x, p1.y + offset.y, p2.x + offset.x, p2.y + offset.y, color, thickness);
  }
}
