package com.hagenberg.jarvis.views;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.debugger.ThreadReferenceProvider;
import com.sun.jdi.ThreadReference;

import imgui.ImGui;

public class ThreadList extends View {

  private ThreadReferenceProvider provider;

  Thread updateThread = new Thread(() -> {
    List<ThreadReference> threads; 
    while ((threads = provider.getThreads()) != null) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      updateThreads(threads);
    }
  });

  private class JThread {
    private String name;
    private boolean isSuspended;
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
      synchronized (this.threads) {
        this.threads.add(t);
      }
    });
  }

  @Override
  protected void renderWindow() {
    synchronized (threads) {
      for (JThread t : threads) {
        ImGui.text(t.name);
        if (t.isSuspended) {
          ImGui.sameLine();
          ImGui.textColored(Colors.Attention, "Suspended");
        }
      }
    }
  }
}
