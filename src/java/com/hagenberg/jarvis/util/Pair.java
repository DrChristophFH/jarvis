package com.hagenberg.jarvis.util;

public class Pair<T, U> {
  private final T first;
  private final U second;

  public Pair(T first, U second) {
    this.first = first;
    this.second = second;
  }

  public T first() {
    return first;
  }

  public U second() {
    return second;
  }

  @Override
  public String toString() {
    return "(" + first + ", " + second + ")";
  }

  @Override
  public int hashCode() {
    return first.hashCode() ^ second.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Pair)) return false;
    Pair<?, ?> pairo = (Pair<?, ?>) o;
    return this.first.equals(pairo.first()) && this.second.equals(pairo.second());
  }
}
