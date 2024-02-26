package com.hagenberg.jarvis.views;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.debugger.ThreadReferenceProvider;
import com.sun.jdi.ThreadReference;

import imgui.ImGui;
import imgui.type.ImInt;

public class ThreadList extends View {

  private ThreadReferenceProvider provider;
  private ImInt pollInterval = new ImInt(1000);

  Thread updateThread = new Thread(() -> {
    List<ThreadReference> threads; 
    while ((threads = provider.getThreads()) != null) {
      try {
        Thread.sleep(pollInterval.get());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      updateThreads(threads);
    }
  });

  private class JThread {
    private String name;
    private boolean isSuspended;
    private String state;
    private int suspendCount;
  }

  private List<JThread> threads = new ArrayList<>();

  public ThreadList() {
    setName("Threads");
    updateThread.setDaemon(true);
    updateThread.setName("VM Thread State Poll");
  }

  public void setProvider(ThreadReferenceProvider provider) {
    this.provider = provider;
  }

  public void start() {
    updateThread.start();
  }

  public void updateThreads(List<ThreadReference> threads) {
    synchronized (this.threads) {
      this.threads.clear();
    }
    threads.forEach(thread -> {
      JThread t = new JThread();
      t.name = thread.name();
      t.isSuspended = thread.isSuspended();
      switch (thread.status()) {
        case ThreadReference.THREAD_STATUS_MONITOR -> t.state = "Waiting on monitor";
        case ThreadReference.THREAD_STATUS_NOT_STARTED -> t.state = "Not Started";
        case ThreadReference.THREAD_STATUS_RUNNING -> t.state = "Running";
        case ThreadReference.THREAD_STATUS_SLEEPING -> t.state = "Sleeping";
        case ThreadReference.THREAD_STATUS_UNKNOWN -> t.state = "Unknown";
        case ThreadReference.THREAD_STATUS_WAIT -> t.state = "Waiting";
        case ThreadReference.THREAD_STATUS_ZOMBIE -> t.state = "Completed";
      }
      t.suspendCount = thread.suspendCount();
      synchronized (this.threads) {
        this.threads.add(t);
      }
    });
  }

  @Override
  protected void renderWindow() {
    ImGui.inputInt("Poll Interval", pollInterval);
    synchronized (threads) {
      for (JThread t : threads) {
        ImGui.text(t.name);
        if (t.isSuspended) {
          ImGui.sameLine();
          ImGui.textColored(Colors.Attention, "Suspended : " + t.suspendCount);
        }
        ImGui.sameLine();
        ImGui.textColored(Colors.Success, t.state);
      }
    }
  }
}
