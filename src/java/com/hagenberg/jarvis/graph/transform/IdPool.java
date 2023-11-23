package com.hagenberg.jarvis.graph.transform;

public class IdPool implements IdProvider {
  private int initialValue;
  private int id;

  public IdPool(int initialValue) {
    this.initialValue = initialValue;
    this.id = initialValue;
  }

  public int next() {
    return id++;
  }

  public void reset() {
    id = initialValue;
  }

  public void setInitialValue(int initialValue) {
    this.initialValue = initialValue;
  }
}
