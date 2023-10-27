package com.hagenberg.jarvis.util;

public interface Observable {
  void addObserver(Observer observer);
  void removeObserver(Observer observer);
  void notifyObservers();
}
