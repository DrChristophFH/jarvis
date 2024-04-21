package com.hagenberg.debuggee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManyObjectsExample {
  public static void main(String[] args) {
    List<Map<Integer, String>> maps = new ArrayList<Map<Integer, String>>();

    for (int i = 0; i < 5; i++) {
      Map<Integer, String> map = new java.util.HashMap<>();
      maps.add(map);
      for (int j = 0; j < 10; j++) {
        map.put(j+1, "value" + j);
      }
    }

    System.out.println("Done");
  }
}
