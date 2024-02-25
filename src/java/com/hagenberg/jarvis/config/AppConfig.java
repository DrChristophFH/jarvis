package com.hagenberg.jarvis.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.hagenberg.jarvis.models.entities.BreakPoint;

public class AppConfig {
  private Map<String, TransformerConfig> transformers = new HashMap<>();
  private Map<String, List<BreakPoint>> breakPoints = new TreeMap<>();
  private String classPath = "target/classes";
  private String mainClass = "test.Main";
  private List<String> sourcePaths = new ArrayList<>();
  private List<String> specialClasses = new ArrayList<>() {
    {
      add("java.lang.Class");
      add("java.lang.invoke.DirectMethodHandle");
    }
  };

  public Map<String, TransformerConfig> getTransformers() {
    return transformers;
  }

  public Map<String, List<BreakPoint>> getBreakPoints() {
    return breakPoints;
  }

  public String getClassPath() {
    return classPath;
  }

  public String getMainClass() {
    return mainClass;
  }

  public List<String> getSourcePaths() {
    return sourcePaths;
  }

  public List<String> getSpecialClasses() {
    return specialClasses;
  }

  public void setTransformers(Map<String, TransformerConfig> transformers) {
    this.transformers = transformers;
  }

  public void setBreakPoints(Map<String, List<BreakPoint>> breakPoints) {
    this.breakPoints = breakPoints;
  }

  public void setClassPath(String classPath) {
    this.classPath = classPath;
  }

  public void setMainClass(String mainClass) {
    this.mainClass = mainClass;
  }

  public void setSourcePaths(List<String> sourcePaths) {
    this.sourcePaths = sourcePaths;
  }

  public void setSpecialClasses(List<String> specialClasses) {
    this.specialClasses = specialClasses;
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
