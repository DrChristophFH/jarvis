package com.hagenberg.interaction;

@FunctionalInterface
public interface Action<T> {
  void execute(T payload);
}
