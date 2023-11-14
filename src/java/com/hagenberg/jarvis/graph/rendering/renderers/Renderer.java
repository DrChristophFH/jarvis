package com.hagenberg.jarvis.graph.rendering.renderers;

import java.util.List;

import com.hagenberg.imgui.components.Tooltip;
import com.hagenberg.jarvis.graph.rendering.Link;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;

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
  
  public abstract void render(T node, int id, List<Link> links);

  public boolean isApplicable(T node) {
    return true;
  }
}