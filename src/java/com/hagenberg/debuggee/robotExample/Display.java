package com.hagenberg.debuggee.robotExample;

public class Display {
  private String displayedText = "";

  public void display(String text) {
    displayedText = text;
  }

  public String read() {
    return displayedText;
  }
}
