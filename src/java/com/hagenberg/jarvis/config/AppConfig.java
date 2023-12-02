package com.hagenberg.jarvis.config;

import java.util.HashMap;
import java.util.Map;

public class AppConfig {
  private Map<String, TransformerConfig> transformers = new HashMap<>();

  // Getters and setters for transformers
  public Map<String, TransformerConfig> getTransformers() {
    return transformers;
  }

  public void setTransformers(Map<String, TransformerConfig> transformers) {
    this.transformers = transformers;
  }

  public static class TransformerConfig {
    private String name;
    private String type;

    public String getName() {
      return name;
    }

    public String getType() {
      return type;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setType(String type) {
      this.type = type;
    }
  }

  public static class TemplateTransformerConfig extends TransformerConfig {
    private String[] paths;

    public String[] getPaths() {
      return paths;
    }

    public void setPaths(String[] paths) {
      this.paths = paths;
    }
  }
}
