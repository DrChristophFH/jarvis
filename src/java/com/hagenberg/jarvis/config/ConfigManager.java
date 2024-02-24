package com.hagenberg.jarvis.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class ConfigManager {
  private static ConfigManager instance = null;

  private TagInspector tagInspector;
  private LoaderOptions loaderOptions = new LoaderOptions();
  private Yaml yaml;
  private String filePath = "config.yaml";
  private AppConfig config = new AppConfig();

  private ConfigManager() {
    tagInspector = tag -> tag.getClassName().startsWith("com.hagenberg.jarvis");
    loaderOptions.setTagInspector(tagInspector);
    yaml = new Yaml(new Constructor(AppConfig.class, loaderOptions));
    loadConfig();
  }

  public static ConfigManager getInstance() {
    if (instance == null) {
      instance = new ConfigManager();
    }
    return instance;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public AppConfig getConfig() {
    return config;
  }

  public void saveConfig() {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.write(yaml.dumpAs(config, Tag.MAP, DumperOptions.FlowStyle.BLOCK));
    } catch (IOException e) {
      System.out.println("Could not save config file.");
    }
  }

  public void loadConfig() {
    try (FileReader reader = new FileReader(filePath)) {
      config = yaml.loadAs(reader, AppConfig.class);
    } catch (IOException e) {
      System.out.println("No config file found. Creating default config.");
    }
  }
}