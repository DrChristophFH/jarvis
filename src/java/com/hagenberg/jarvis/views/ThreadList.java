package com.hagenberg.jarvis.views;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.View;
import com.sun.jdi.ThreadReference;

import imgui.ImGui;

public class ThreadList extends View {

  private class Thread {
    private String name;
    private boolean isSuspended;
  }

  private List<Thread> threads = new ArrayList<>();

  public ThreadList() {
    setName("Threads");
  }

  public void updateThreads(List<ThreadReference> threads) {
    synchronized (this.threads) {
      this.threads.clear();
    }
    threads.forEach(thread -> {
      Thread t = new Thread();
      t.name = thread.name();
      t.isSuspended = thread.isSuspended();
      synchronized (this.threads) {
        this.threads.add(t);
      }
    });
  }

  @Override
  protected void renderWindow() {
    synchronized (threads) {
      for (Thread t : threads) {
        ImGui.text(t.name);
        if (t.isSuspended) {
          ImGui.sameLine();
          ImGui.textColored(Colors.Attention, "Suspended");
        }
      }
    }
  }
}
