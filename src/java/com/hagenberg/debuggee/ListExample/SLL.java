package com.hagenberg.debuggee.ListExample;

public class SLL {
  private SLLNode head;

  public void add(int data) {
    SLLNode newNode = new SLLNode(data);

    if (head == null) {
      head = newNode;
    } else {
      SLLNode current = head;
      while (current.getNext() != null) {
        current = current.getNext();
      }
      current.setNext(newNode);
    }
  }

  public void prepend(int data) {
    SLLNode newNode = new SLLNode(data);
    newNode.setNext(head);
    head = newNode;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    SLLNode current = head;
    while (current != null) {
      sb.append(current.getData());
      sb.append(" ");
      current = current.getNext();
    }
    return sb.toString();
  }
}
