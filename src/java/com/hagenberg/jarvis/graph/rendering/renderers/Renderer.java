package com.hagenberg.jarvis.graph.rendering.renderers;

import com.hagenberg.imgui.components.Tooltip;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.views.ObjectGraph;

public abstract class Renderer<T> {
  private final Class<T> supportedClass;
  protected String name;
  protected RendererRegistry registry;
  protected Tooltip tooltip = new Tooltip();

  public Renderer(Class<T> supportedClass, String name, RendererRegistry registry) {
    this.supportedClass = supportedClass;
    this.name = name;
    this.registry = registry;
  }
  
  public String getName() {
    return name;
  }
  
  public boolean canHandle(Object node) {
    return supportedClass.isInstance(node);
  }
  
  public abstract void render(T node, int id, ObjectGraph graph);

  public boolean isApplicable(T node) {
    return true;
  }
}