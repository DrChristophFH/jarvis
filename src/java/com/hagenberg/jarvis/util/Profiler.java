package com.hagenberg.jarvis.util;

import java.util.HashMap;
import java.util.Map;

import imgui.ImGui;

public class Profiler {
  private static Map<String, Long> startTimes = new HashMap<>();
  private static Map<String, Long> nanoAcc = new HashMap<>(); 

  public static void show() {
    ImGui.begin("Profiler");
    for (String key : nanoAcc.keySet()) {
      ImGui.text("%-20s: %6.4f ms".formatted(key, nanoAcc.get(key) / 1000000.0));
      nanoAcc.put(key, 0L);
    }
    ImGui.end();
  }

  public static void start(String name) {
    startTimes.put(name, System.nanoTime());
  }

  public static void stop(String name) {
    long stopTime = System.nanoTime();
    long startTime = startTimes.get(name);
    long duration = stopTime - startTime;
    if (nanoAcc.containsKey(name)) {
      nanoAcc.put(name, nanoAcc.get(name) + duration);
    } else {
      nanoAcc.put(name, duration);
    }
  }
}
