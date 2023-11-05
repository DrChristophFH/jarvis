package com.hagenberg.imgui;

import imgui.ImVec2;

public class Vec2 {
  public float x;
  public float y;

  public Vec2() {
    this(0, 0);
  }

  public Vec2(ImVec2 imVec2) {
    this(imVec2.x, imVec2.y);
  }

  public Vec2(Vec2 other) {
    this(other.x, other.y);
  }

  public Vec2(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Vec2 add(Vec2 other) {
    this.x += other.x;
    this.y += other.y;
    return this; 
  }

  
  public Vec2 add(ImVec2 other) {
    this.x += other.x;
    this.y += other.y;
    return this; 
  }

  public Vec2 add(float x, float y) {
    this.x += x;
    this.y += y;
    return this; 
  }

  public Vec2 add(float scalar) {
    this.x += scalar;
    this.y += scalar;
    return this; 
  }

  public Vec2 subtract(Vec2 other) {
    this.x -= other.x;
    this.y -= other.y;
    return this; 
  }

  public Vec2 subtract(ImVec2 other) {
    this.x -= other.x;
    this.y -= other.y;
    return this; 
  }

  public Vec2 subtract(float x, float y) {
    this.x -= x;
    this.y -= y;
    return this; 
  }

  public Vec2 subtract(float scalar) {
    this.x -= scalar;
    this.y -= scalar;
    return this; 
  }

  public Vec2 scale(float scalar) {
    this.x *= scalar;
    this.y *= scalar;
    return this; 
  }

  public Vec2 scaleX(float scalar) {
    this.x *= scalar;
    return this; 
  }

  public Vec2 scaleY(float scalar) {
    this.y *= scalar;
    return this; 
  }

  public Vec2 addDirection(float angle, float distance) {
    this.x += Math.cos(angle) * distance;
    this.y += Math.sin(angle) * distance;
    return this; 
  }

  public Vec2 clear() {
    this.x = 0;
    this.y = 0;
    return this; 
  }

  public Vec2 set(Vec2 other) {
    this.x = other.x;
    this.y = other.y;
    return this; 
  }

  public Vec2 clampAbs(float max) {
    if (Math.abs(this.x) > max) {
      this.x = Math.signum(this.x) * max;
    }
    if (Math.abs(this.y) > max) {
      this.y = Math.signum(this.y) * max;
    }
    return this; 
  }

  @Override
  public String toString() {
    return "Vec2(" + x + ", " + y + ")";
  }
}
