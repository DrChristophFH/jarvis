package com.hagenberg.jarvis.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IndexedList<I, T> implements Iterable<T> {
  private I index;
  private List<T> list = new ArrayList<>();

  public IndexedList(I index) {
    this.index = index;
  }

  public IndexedList(I index, List<T> list) {
    this.index = index;
    this.list = list;
  }

  public I getIndex() {
    return index;
  }

  public List<T> getList() {
    return list;
  }

  public void add(T element) {
    list.add(element);
  }

  public void remove(T element) {
    list.remove(element);
  }

  public void clear() {
    list.clear();
  }

  public int size() {
    return list.size();
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  @Override
  public Iterator<T> iterator() {
    return list.iterator();
  }
}
