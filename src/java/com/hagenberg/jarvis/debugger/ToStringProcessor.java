package com.hagenberg.jarvis.debugger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.util.Logger;
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
  private final Logger log = Logger.getInstance();
  private CountDownLatch stopLatch;
  private ThreadReference currentThread;

  public ToStringProcessor() {
    this.setName("ToStringProcessor");
    this.setDaemon(true);
    this.queue = new ConcurrentLinkedQueue<>();
    this.processing = new AtomicBoolean(false);
    this.stopLatch = new CountDownLatch(1);
  }

  public void addTask(Pair<JObjectReference, ObjectReference> task) {
    queue.add(task);
  }

  public void signalToStart(ThreadReference currentThread) {
    synchronized (lock) {
      stopLatch = new CountDownLatch(1); // Reset the latch for the next cycle
      processing.set(true);
      this.currentThread = currentThread;
      lock.notify(); // Notify to start processing
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
  }

  public void clear() {
    queue.clear();
  }

  @Override
  public void run() {
    try {
      while (true) {
        synchronized (lock) {
          while (!processing.get()) {
            lock.wait(); // Wait for signal to start processing
          }
        }

        while (processing.get() && !queue.isEmpty()) {
          Pair<JObjectReference, ObjectReference> task = queue.poll();
          if (task != null) {
            resolveToString(task.first(), task.second());
          }
        }
        // Signal that processing has stopped
        stopLatch.countDown();
        processing.set(false);
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
      List<Method> methods = objRef.referenceType().methodsByName("toString", "()Ljava/lang/String;");
      if (!methods.isEmpty()) {
        Method toStringMethod = methods.get(0);

        var declType = toStringMethod.declaringType().name();

        // skip base object toString() method
        if (!declType.equals("java.lang.Object")) {
          int flags = 0;
          result = objRef.invokeMethod(currentThread, toStringMethod, new ArrayList<>(), flags).toString();
        }
      }
    } catch (IllegalArgumentException | InvalidTypeException | ClassNotLoadedException | IncompatibleThreadStateException | InvocationException e) {
      result = "toString() not available";
      log.logWarning("Error invoking toString() on object: " + objRef + " - " + e.getMessage());
    }
    node.setToString(result);
  }
}
