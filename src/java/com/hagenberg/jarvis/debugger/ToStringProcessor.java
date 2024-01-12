package com.hagenberg.jarvis.debugger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.util.Pair;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;

import java.util.concurrent.CountDownLatch;

public class ToStringProcessor extends Thread {
  private final ConcurrentLinkedQueue<Pair<JObjectReference, ObjectReference>> queue;
  private final AtomicBoolean processing;
  private final Object lock = new Object();
  private CountDownLatch stopLatch;
  private ThreadReference currentThread;

  public ToStringProcessor() {
    this.queue = new ConcurrentLinkedQueue<>();
    this.processing = new AtomicBoolean(false);
    this.stopLatch = new CountDownLatch(1);
  }

  public void addTask(Pair<JObjectReference, ObjectReference> task) {
    queue.add(task);
  }

  public void signalToStart(ThreadReference currentThread) {
    synchronized (lock) {
      processing.set(true);
      this.currentThread = currentThread;
      lock.notify(); // Notify to start processing
      System.out.println("ToStringProcessor: notified start");
    }
  }

  public void stopProcessing() {
    processing.set(false);
  }

  public boolean isProcessing() {
    return processing.get();
  }

  public void waitForStopSignal() throws InterruptedException {
    stopLatch.await();
    System.out.println("ToStringProcessor: stopped");
  }

  public void clear() {
    queue.clear();
  }

  @Override
  public void run() {
    try {
      while (true) {
        synchronized (lock) {
          while (!processing.get() || queue.isEmpty()) {
            lock.wait(); // Wait for signal to start processing
          }
        }

        System.out.println("ToStringProcessor: started");

        while (processing.get() || !queue.isEmpty()) {
          Pair<JObjectReference, ObjectReference> task = queue.poll();
          if (task != null) {
            System.out.println("ToStringProcessor: processing task");
            resolveToString(task.first(), task.second());
          }
        }

        System.out.println("ToStringProcessor: finished");

        // Signal that processing has stopped
        stopLatch.countDown();
        stopLatch = new CountDownLatch(1); // Reset the latch for the next cycle
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * <b>NOTE:</b> This method will <b>RESUME</b> the current thread, therefore
   * invalidating all stack frames!
   */
  private void resolveToString(JObjectReference node, ObjectReference objRef) {
    String result = null;
    try {

      System.out.println("ToStringProcessor: resolving toString() for " + node.name());

      List<Method> methods = objRef.referenceType().methodsByName("toString", "()Ljava/lang/String;");
      if (!methods.isEmpty()) {
        Method toStringMethod = methods.get(0);

        var declType = toStringMethod.declaringType().name();

        // skip base object toString() method
        if (!declType.equals("java.lang.Object")) {
          int flags = 0;
          System.out.println("ToStringProcessor: invoking toString() for " + node.name());
          result = objRef.invokeMethod(currentThread, toStringMethod, new ArrayList<>(), flags).toString();
        }
      }
    } catch (IllegalArgumentException | InvalidTypeException | ClassNotLoadedException | IncompatibleThreadStateException
    | InvocationException e) {
      result = "toString() not available";
    }
    node.setToString(result);
    System.out.println("ToStringProcessor: toString() for " + node.name() + " resolved to " + result);
  }
}
