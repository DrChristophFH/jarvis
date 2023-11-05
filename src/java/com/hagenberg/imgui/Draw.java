package com.hagenberg.imgui;

import java.util.Stack;

import imgui.ImDrawList;
import imgui.ImGui;

public class Draw {
  private final ImDrawList dList;
  private Vec2 scroll;
  private Stack<Vec2> offsetStack = new Stack<>();
  private Vec2 offset = new Vec2();
  
  private Stack<Bounder> bounderStack = new Stack<>();

  public Draw(ImDrawList dList) {
    this(dList, new Vec2(0, 0));
  }

  public Draw(ImDrawList dList, Vec2 offset) {
    this.dList = dList;
    this.scroll = offset;
  }

  // +---------------------------------------------------------------------------+
  // +                        Scroll and Offset                                  +
  // +---------------------------------------------------------------------------+

  /**
   * Sets the offset of the drawing. Used for scrolling/panning.
   *
   * @param offset the offset to set
   */
  public void setScroll(Vec2 offset) {
    this.scroll = offset;
  }

  /**
   * Pushes a new offset onto the offset stack and adds it to the current offset.
   * @param pos the position to be pushed onto the offset stack
   */
  public void pushOffset(Vec2 pos) {
    offsetStack.push(pos);
    offset.add(pos);
    boundersAddOffset(pos);
  }

  /**
   * Pops the last offset from the offset stack and subtracts it from the current offset.
   */
  public void popOffset() {
    Vec2 popped = offsetStack.pop();
    offset.subtract(popped);
    boundersSubtractOffset(popped);
  }

  /**
   * Returns a new Vec2 with the given position and the current offset subtracted.
   * @param pos the position to be adjusted
   * @return adjusted position
   */
  public Vec2 absolute(Vec2 pos) {
    return new Vec2(pos).subtract(offset);
  }

  // +---------------------------------------------------------------------------+
  // +                        Bounder                                            +
  // +---------------------------------------------------------------------------+

  /**
   * Pushes a new bounder onto the bounder stack.
   * @param bounder the bounder to be pushed onto the stack
   */
  public void pushBounder(Bounder bounder) {
    bounderStack.push(bounder);
  }

  public Vec2 popBounder() {
    return bounderStack.pop().getBound();
  }

  public void boundersAddOffset(Vec2 offset) {
    for (Bounder bounder : bounderStack) {
      bounder.addOffset(offset);
    }
  }

  public void boundersSubtractOffset(Vec2 offset) {
    for (Bounder bounder : bounderStack) {
      bounder.subtractOffset(offset);
    }
  }

  public void calcBoundStack(Vec2 bottomRight) {
    for (Bounder bounder : bounderStack) {
      bounder.bound(bottomRight);
    }
  }

  public void addRectB(Vec2 p0, Vec2 p1, int color) {
    dList.addRect(p0.x + scroll.x + offset.x, p0.y + scroll.y + offset.y, p1.x + scroll.x + offset.x, p1.y + scroll.y + offset.y, color);
    calcBoundStack(p1);
  }

  // +---------------------------------------------------------------------------+
  // +                        Clip Rects                                         +
  // +---------------------------------------------------------------------------+


  /**
   * Pushes a new clip rectangle onto the stack.
   *
   * @param p0 the top-left corner of the rectangle
   * @param p1 the bottom-right corner of the rectangle
   * @param intersectWithCurrentClipRect if true, the new clip rectangle will be intersected with the current clip rectangle
   */
  public void pushClipRect(Vec2 p0, Vec2 p1, boolean intersectWithCurrentClipRect) {
    dList.pushClipRect(p0.x + scroll.x + offset.x, p0.y + scroll.y + offset.y, p1.x + scroll.x + offset.x, p1.y + scroll.y + offset.y, intersectWithCurrentClipRect);
  }

  /**
   * Pops the last clip rectangle from the stack.
   */
  public void popClipRect() {
    dList.popClipRect();
  }


  // +---------------------------------------------------------------------------+
  // +                        Drawing                                            +
  // +---------------------------------------------------------------------------+

  /**
   * Adds a filled rectangle to the drawing list.
   *
   * @param p0 the top-left corner of the rectangle
   * @param p1 the bottom-right corner of the rectangle
   * @param color the color of the rectangle as ImGuiCol
   */
  public void addRectFilled(Vec2 p0, Vec2 p1, int color) {
    dList.addRectFilled(p0.x + scroll.x + offset.x, p0.y + scroll.y + offset.y, p1.x + scroll.x + offset.x, p1.y + scroll.y + offset.y, color);
  }


  /**
   * Adds a rectangle to the drawing list.
   *
   * @param p0 the top-left corner of the rectangle
   * @param p1 the bottom-right corner of the rectangle
   * @param color the color of the rectangle as ImGuiCol
   */
  public void addRect(Vec2 p0, Vec2 p1, int color) {
    dList.addRect(p0.x + scroll.x + offset.x, p0.y + scroll.y + offset.y, p1.x + scroll.x + offset.x, p1.y + scroll.y + offset.y, color);
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
    dList.addRect(p0.x + scroll.x + offset.x, p0.y + scroll.y + offset.y, p1.x + scroll.x + offset.x, p1.y + scroll.y + offset.y, color, 0, 0, lineWidth);
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
    dList.addCircleFilled(origin.x + scroll.x + offset.x, origin.y + scroll.y + offset.y, radius, color, segments);
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
    dList.addCircle(origin.x + scroll.x + offset.x, origin.y + scroll.y + offset.y, radius, color, segments);
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
  public void addLine(Vec2 p1, Vec2 p2, int color) {
    dList.addLine(p1.x + scroll.x + offset.x, p1.y + scroll.y + offset.y, p2.x + scroll.x + offset.x, p2.y + scroll.y + offset.y, color);
  }

  /**
   * Adds text to the drawing list.
   *
   * @param position the position to draw the text
   * @param color the color of the text as ImGuiCol
   * @param text the text to be displayed
   * @param textSize the size of the text
   */
  public void addText(Vec2 position, int color, String text, float textSize) {
    dList.addText(ImGui.getFont(), textSize, position.x + scroll.x + offset.x, position.y + scroll.y + offset.y, color, text);
  }
}
