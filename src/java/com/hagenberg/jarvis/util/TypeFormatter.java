package com.hagenberg.jarvis.util;

public class TypeFormatter {
  public static String getSimpleType(String type) {
    String[] parts = type.split("\\.");
    return parts[parts.length - 1];
  }
}
