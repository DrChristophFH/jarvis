package com.hagenberg.jarvis.graph.transform;

import com.hagenberg.jarvis.graph.transform.simple.SimpleLocalVariableTransformer;
import com.hagenberg.jarvis.graph.transform.simple.SimpleMemberTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hagenberg.jarvis.graph.transform.simple.SimpleContentTransformer;
import com.hagenberg.jarvis.graph.transform.simple.SimpleObjectTransformer;
import com.hagenberg.jarvis.graph.transform.specific.StringObjectTransformer;
import com.hagenberg.jarvis.graph.transform.specific.TemplateObjectTransformer;
import com.hagenberg.jarvis.models.entities.graph.ContentGVariable;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

public class TransformerRegistry {

  private final TransformerContextMenu transformerContextMenu;
  
  // default transformers
  private NodeTransformer<ObjectGNode> defaultObjectRenderer;
  private NodeTransformer<LocalGVariable> defaultLocalVarRenderer = new SimpleLocalVariableTransformer();
  private AttributeTransformer<MemberGVariable> defaultMemberRenderer = new SimpleMemberTransformer();
  private AttributeTransformer<ContentGVariable> defaultContentRenderer = new SimpleContentTransformer();
  
  // transformer lists
  private List<TemplateObjectTransformer> templateTransformers = new ArrayList<>();
  private List<NodeTransformer<ObjectGNode>> objectTransformers = new ArrayList<>();
  
  // transformer mapping
  private Map<Object, NodeTransformer<ObjectGNode>> objectTransformerMap = new HashMap<>();

  public TransformerRegistry(TransformerContextMenu transformerContextMenu) {
    this.transformerContextMenu = transformerContextMenu;
    defaultObjectRenderer = new SimpleObjectTransformer(this, this.transformerContextMenu);
    objectTransformers.add(new StringObjectTransformer(this.transformerContextMenu));
  }

  public NodeTransformer<LocalGVariable> getLocalVarTransformer(LocalGVariable localVar) {
    return defaultLocalVarRenderer;
  }

  // ------------ Object transformers ------------

  public NodeTransformer<ObjectGNode> getObjectTransformer(ObjectGNode object) {
    NodeTransformer<ObjectGNode> transformer;

    // first fetch for specific object
    transformer = objectTransformerMap.get(object);

    // then fetch for object class
    if (transformer == null) {
      transformer = objectTransformerMap.get(object.getType());

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
  public NodeTransformer<ObjectGNode> getSpecificOT(ObjectGNode object) {
    NodeTransformer<ObjectGNode> transformer;

    // first fetch for specific object
    transformer = objectTransformerMap.get(object);

    // then fetch for object class
    if (transformer == null) {
      transformer = objectTransformerMap.get(object.getType());
    }

    return transformer;
  }

  /** 
   * Returns the highest registered transformer for the given object's type or null if no specific transformer is registered.
   * @param object the objectGNode for who's type to get the transformer for
   * @return  the transformer for the given object's type or null if no transformer is registered
   */
  public NodeTransformer<ObjectGNode> getSpecificOTForType(ObjectGNode object) {
    return objectTransformerMap.get(object.getType());
  }

  public List<NodeTransformer<ObjectGNode>> getObjectTransformers() {
    List<NodeTransformer<ObjectGNode>> transformers = new ArrayList<>();
    for (TemplateObjectTransformer transformer : templateTransformers) {
      transformers.add(transformer);
    }
    for (NodeTransformer<ObjectGNode> transformer : objectTransformers) {
      transformers.add(transformer);
    }
    return transformers;
  }

  public void setObjectTransformer(Object objNode, NodeTransformer<ObjectGNode> transformer) {
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
}
