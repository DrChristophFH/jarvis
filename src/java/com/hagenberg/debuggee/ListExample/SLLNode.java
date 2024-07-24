package com.hagenberg.debuggee.ListExample;

public class SLLNode {
  private Integer data;
  private SLLNode next;

  SLLNode(int data) {
    this.data = data;
  }

  public int getData() {
    return data;
  }

  public SLLNode getNext() {
    return next;
  }

  public void setNext(SLLNode next) {
    this.next = next;
  }

  public String toString() {
    return data.toString();
  }
}