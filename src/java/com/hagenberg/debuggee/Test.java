package com.hagenberg.debuggee;

public class Test {

  private static int x = 5;

  public static void main(String[] args) {
    Class c = Test.class;
    System.out.println(c.getName());
    test();
  }

  public static void test() {
    System.out.println("test" + x);
  }
}
