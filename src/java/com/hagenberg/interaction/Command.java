package com.hagenberg.interaction;

public class Command<T> {
  private String name;
  private Action<T> action;

  public Command(String name, Action<T> action) {
    this.name = name;
    this.action = action;
  }

  public void execute(T payload) {
    action.execute(payload);
  }

  public String getName() {
    return name;
  }
}
