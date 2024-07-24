package com.hagenberg.debuggee.ListExample;

public class Main {
  public static void main(String[] args) {
    SLL list = new SLL();

    for (int i = 0; i < 5; i++) {
      list.add(i);
    }

    System.out.println("List created");

    list.prepend(99);

    System.out.println("List prepended");
  }
}
