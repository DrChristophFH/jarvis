package com.hagenberg.jarvis.debugger;

public interface DebugeeConsole {
  void println(String message);
  void registerInputHandler(InputHandler handler);
}
