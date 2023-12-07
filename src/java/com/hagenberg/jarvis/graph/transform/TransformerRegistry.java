package com.hagenberg.jarvis.graph.transform;

import com.hagenberg.jarvis.graph.transform.simple.SimpleLocalVariableTransformer;
import com.hagenberg.jarvis.graph.transform.simple.SimpleMemberTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hagenberg.jarvis.config.AppConfig;
import com.hagenberg.jarvis.config.ConfigManager;
import com.hagenberg.jarvis.config.AppConfig.TransformerConfig;
import com.hagenberg.jarvis.graph.transform.simple.SimpleContentTransformer;
import com.hagenberg.jarvis.graph.transform.simple.SimpleObjectTransformer;
import com.hagenberg.jarvis.graph.transform.specific.StringObjectTransformer;
import com.hagenberg.jarvis.graph.transform.specific.TemplateObjectTransformer;
import com.hagenberg.jarvis.models.entities.graph.ContentGVariable;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JLocalVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;

public class TransformerRegistry {

  private final TransformerContextMenu transformerContextMenu;
  
  // default transformers
  private NodeTransformer<JObjectReference> defaultObjectRenderer;
  private NodeTransformer<JLocalVariable> defaultLocalVarRenderer = new SimpleLocalVariableTransformer();
  private AttributeTransformer<MemberGVariable> defaultMemberRenderer = new SimpleMemberTransformer();
  private AttributeTransformer<ContentGVariable> defaultContentRenderer = new SimpleContentTransformer();
  
  // transformer lists
  private List<TemplateObjectTransformer> templateTransformers = new ArrayList<>();
  private List<NodeTransformer<JObjectReference>> objectTransformers = new ArrayList<>();
  
  // transformer mapping
  private Map<Object, NodeTransformer<JObjectReference>> objectTransformerMap = new HashMap<>();

  public TransformerRegistry(TransformerContextMenu transformerContextMenu) {
    this.transformerContextMenu = transformerContextMenu;
    defaultObjectRenderer = new SimpleObjectTransformer(this, this.transformerContextMenu);
    objectTransformers.add(new StringObjectTransformer(this.transformerContextMenu));
  }

  public NodeTransformer<JLocalVariable> getLocalVarTransformer(JLocalVariable localVar) {
    return defaultLocalVarRenderer;
  }

  // ------------ Object transformers ------------

  public NodeTransformer<JObjectReference> getObjectTransformer(JObjectReference object) {
    NodeTransformer<JObjectReference> transformer;

    // first fetch for specific object
    transformer = objectTransformerMap.get(object);

    // then fetch for object class
    if (transformer == null) {
      transformer = objectTransformerMap.get(object.type());

      // return default if no specific transformer is found
      if (transformer == null) {
        transformer = defaultObjectRenderer;
      }
    }

    return transformer;
  }

  /**
   * Returns the highest registered transformer or null if no specific transformer is registered.
   * @param object the objectGNode to get the transformer for
   * @return the transformer for the given object or null if no transformer is registered
   */
  public NodeTransformer<JObjectReference> getSpecificOT(JObjectReference object) {
    NodeTransformer<JObjectReference> transformer;

    // first fetch for specific object
    transformer = objectTransformerMap.get(object);

    // then fetch for object class
    if (transformer == null) {
      transformer = objectTransformerMap.get(object.type());
    }

    return transformer;
  }

  /** 
   * Returns the highest registered transformer for the given object's type or null if no specific transformer is registered.
   * @param object the objectGNode for who's type to get the transformer for
   * @return  the transformer for the given object's type or null if no transformer is registered
   */
  public NodeTransformer<JObjectReference> getSpecificOTForType(JObjectReference object) {
    return objectTransformerMap.get(object.type());
  }

  public List<NodeTransformer<JObjectReference>> getObjectTransformers() {
    List<NodeTransformer<JObjectReference>> transformers = new ArrayList<>();
    for (TemplateObjectTransformer transformer : templateTransformers) {
      transformers.add(transformer);
    }
    for (NodeTransformer<JObjectReference> transformer : objectTransformers) {
      transformers.add(transformer);
    }
    return transformers;
  }

  public void setObjectTransformer(Object objNode, NodeTransformer<JObjectReference> transformer) {
    if (transformer == defaultObjectRenderer || transformer == null) {
      objectTransformerMap.remove(objNode);
    } else {
      objectTransformerMap.put(objNode, transformer);
    }
  }

  // ------------ Attribute transformers ------------

  public AttributeTransformer<MemberGVariable> getMemberTransformer(MemberGVariable member) {
    return defaultMemberRenderer;
  }

  public AttributeTransformer<ContentGVariable> getContentTransformer(ContentGVariable content) {
    return defaultContentRenderer;
  }

  // ------------ Template transformers ------------

  public List<TemplateObjectTransformer> getTemplates() {
    return templateTransformers;
  }

  public void registerTemplate(String name) {
    templateTransformers.add(new TemplateObjectTransformer(name, this, transformerContextMenu));
  }


  // ------------ Save / Load ------------

  public void load() {
    ConfigManager configManager = ConfigManager.getInstance();
    AppConfig config = configManager.getConfig();
    Map<String, TransformerConfig> transformersConfig = config.getTransformers();
    for (TransformerConfig transformerConfig : transformersConfig.values()) {
      if (transformerConfig instanceof AppConfig.TemplateTransformerConfig) {
        AppConfig.TemplateTransformerConfig templateConfig = (AppConfig.TemplateTransformerConfig) transformerConfig;
        TemplateObjectTransformer transformer = new TemplateObjectTransformer(templateConfig.getName(), this, transformerContextMenu);
        for (String path : templateConfig.getPaths()) {
          transformer.getPaths().add(new Path(path));
        }
        templateTransformers.add(transformer);
      }
    }
  }

  public void save() {
    ConfigManager configManager = ConfigManager.getInstance();
    AppConfig config = configManager.getConfig();
    Map<String, TransformerConfig> transformersConfig = config.getTransformers();
    transformersConfig.clear();
    for (TemplateObjectTransformer transformer : templateTransformers) {
      transformersConfig.put(transformer.getName(), transformer.getConfig());
    }
    configManager.saveConfig();
  }
}
