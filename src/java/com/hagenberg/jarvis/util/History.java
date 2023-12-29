package com.hagenberg.jarvis.util;

public class History<T> {
  private Object[] buffer;
  private int head;
  private int traveller;
  private int tail;

  public History(int size) {
    buffer = new Object[size];
    head = 0;
    traveller = 0;
    tail = 0;
  }

  public void push(T item) {
    traveller = (traveller + 1) % buffer.length;
    buffer[traveller] = item;
    head = traveller;
    if (head == tail) {
      tail = (tail + 1) % buffer.length;
    }
  }

  public T current() {
    return (T) buffer[traveller];
  }

  public void back() {
    if (traveller != tail) {
      traveller = (traveller - 1 + buffer.length) % buffer.length;
    }
  }

  public void forward() {
    if (traveller != head) {
      traveller = (traveller + 1) % buffer.length;
    }
  }

  public boolean canGoBack() {
    return traveller != tail;
  }

  public boolean canGoForward() {
    return traveller != head;
  }

  public int size() {
    return buffer.length;
  }
}
