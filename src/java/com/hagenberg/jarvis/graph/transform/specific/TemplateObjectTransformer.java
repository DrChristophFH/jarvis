package com.hagenberg.jarvis.graph.transform.specific;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.jarvis.config.AppConfig;
import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.render.nodes.TemplateObjectNode;
import com.hagenberg.jarvis.graph.transform.IdProvider;
import com.hagenberg.jarvis.graph.transform.LinkRegisterCallback;
import com.hagenberg.jarvis.graph.transform.NodeTransformer;
import com.hagenberg.jarvis.graph.transform.Path;
import com.hagenberg.jarvis.graph.transform.TransformerContextMenu;
import com.hagenberg.jarvis.graph.transform.TransformerRegistry;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;

public class TemplateObjectTransformer extends NodeTransformer<JObjectReference> {

  private List<Path> paths = new ArrayList<>();
  private TransformerRegistry registry;
  private TransformerContextMenu transformerContextMenu;

  public TemplateObjectTransformer(String name, TransformerRegistry registry, TransformerContextMenu transformerContextMenu) {
    this.name = name;
    this.registry = registry;
    this.transformerContextMenu = transformerContextMenu;
  }

  @Override
  public TemplateObjectNode transform(JObjectReference object, IdProvider idProvider, LinkRegisterCallback linkRegisterCallback) {
    TemplateObjectNode objNode = new TemplateObjectNode(
      idProvider.next(), 
      object.getTypeName(), 
      "Object#" + object.getObjectId(), 
      object.getToString(), 
      object.getReferenceHolders(),
      transformerContextMenu
    );
    
    for (Path path : paths) {
      MemberGVariable member = path.resolve(object);
      if (member != null) {
        Attribute att = registry.getMemberTransformer(member).transform(member, idProvider, objNode, linkRegisterCallback);
        objNode.addAttribute(att);
      }
    }

    return objNode;
  }

  public List<Path> getPaths() {
    return paths;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AppConfig.TemplateTransformerConfig getConfig() {
    AppConfig.TemplateTransformerConfig config = new AppConfig.TemplateTransformerConfig();
    config.setName(name);
    config.setType("template");
    config.setPaths(paths.stream().map(Path::toString).toArray(String[]::new));
    return config;
  }
}
