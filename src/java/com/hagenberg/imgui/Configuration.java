package com.hagenberg.imgui;

public class Configuration {
  /**
   * Application title.
   */
  private String title = "ImGui Java Application";
  /**
   * Application window width.
   */
  private int width = 1280;
  /**
   * Application window height.
   */
  private int height = 768;
  /**
   * When true, application will be maximized by default.
   */
  private boolean fullScreen = false;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public boolean isFullScreen() {
    return fullScreen;
  }

  public void setFullScreen(boolean fullScreen) {
    this.fullScreen = fullScreen;
  }
}