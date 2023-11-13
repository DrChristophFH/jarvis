package com.hagenberg.jarvis.util;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;

public class Profiler {
  // moving average of frame duration
  private static final int MAX_SAMPLES = 100;
  private static int frameIndex = 0;
  private static long frameSum = 0;
  private static long[] frameTimes = new long[MAX_SAMPLES];
  private static long frameStart = 0;
  // individual duration counters
  private static Map<String, Long> startTimes = new HashMap<>();
  private static SortedMap<String, Long> nanoAcc = new TreeMap<>();
  private static SortedMap<String, Long> nanoMax = new TreeMap<>();

  public static void show() {
    long frameEnd = System.nanoTime();
    long frameDuration = frameEnd - frameStart;

    long avgFrameDuration = avgFrameDuration(frameDuration);

    ImGui.begin("Profiler");
    ImGui.text("Frame time: %10.4f ms".formatted(avgFrameDuration / 1000000.0));
    ImGui.text("Frame rate: %10.0f fps".formatted(1000000000.0 / avgFrameDuration));

    int tableFlags = ImGuiTableFlags.RowBg | ImGuiTableFlags.Resizable | ImGuiTableFlags.Reorderable | ImGuiTableFlags.Hideable;

    if (ImGui.beginTable("table", 3, tableFlags)) {
      ImGui.tableSetupColumn("Name");
      ImGui.tableSetupColumn("Acc Time");
      ImGui.tableSetupColumn("Max Acc Time");

      for (String key : nanoAcc.keySet()) {
        ImGui.tableNextRow();
        ImGui.tableNextColumn();
        ImGui.text(key);
        ImGui.tableNextColumn();
        ImGui.text("%7.4f ms".formatted(nanoAcc.get(key) / 1000000.0));
        ImGui.tableNextColumn();
        ImGui.text("%7.4f ms".formatted(nanoMax.get(key) / 1000000.0));
        nanoAcc.put(key, 0L);
      }

      ImGui.endTable();
    }

    ImGui.end();
    frameStart = System.nanoTime();
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
    if (nanoMax.containsKey(name)) {
      nanoMax.put(name, Math.max(nanoMax.get(name), duration));
    } else {
      nanoMax.put(name, duration);
    }
  }

  private static long avgFrameDuration(long newDuration) {
    frameSum -= frameTimes[frameIndex];
    frameSum += newDuration;
    frameTimes[frameIndex] = newDuration;
    frameIndex = (frameIndex + 1) % MAX_SAMPLES;
    return frameSum / MAX_SAMPLES;
  }
}
