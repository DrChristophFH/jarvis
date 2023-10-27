package com.hagenberg.jarvis.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hagenberg.jarvis.util.Observable;
import com.hagenberg.jarvis.util.Observer;

public class LayoutGraph implements Observable {
  private Map<Long, LayoutNode> nodes = new HashMap<>();
  private List<LayoutEdge> edges = new ArrayList<>();

  private List<Observer> observers = new ArrayList<>();

  public void addNode(int x, int y, long id) {
    LayoutNode node = new LayoutNode(x, y, id);
    nodes.put(node.id, node);
  }

  public void addEdge(long source, long target) {
    LayoutNode sourceNode = nodes.get(source);
    LayoutNode targetNode = nodes.get(target);
    edges.add(new LayoutEdge(sourceNode, targetNode));
    sourceNode.neighbors.add(targetNode);
    targetNode.neighbors.add(sourceNode);
  }

  public Iterable<LayoutNode> getNodes() {
    return nodes.values();
  }

  public Iterable<LayoutEdge> getEdges() {
    return edges;
  }

  @Override
  public void addObserver(Observer observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers() {
    for (Observer observer : observers) {
      observer.update();
    }
  }
}
