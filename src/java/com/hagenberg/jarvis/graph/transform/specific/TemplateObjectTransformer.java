package com.hagenberg.jarvis.graph.transform.specific;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.render.nodes.DefaultObjectNode;
import com.hagenberg.jarvis.graph.transform.IdProvider;
import com.hagenberg.jarvis.graph.transform.LinkRegisterCallback;
import com.hagenberg.jarvis.graph.transform.NodeTransformer;
import com.hagenberg.jarvis.graph.transform.Path;
import com.hagenberg.jarvis.graph.transform.TransformerRegistry;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

public class TemplateObjectTransformer extends NodeTransformer<ObjectGNode> {

  private List<Path> paths = new ArrayList<>();
  private TransformerRegistry registry;

  public TemplateObjectTransformer(String name, TransformerRegistry registry) {
    this.name = name;
    this.registry = registry;
  }

  @Override
  public DefaultObjectNode transform(ObjectGNode object, IdProvider idProvider, LinkRegisterCallback linkRegisterCallback) {
    DefaultObjectNode objNode = new DefaultObjectNode(
      idProvider.next(), 
      object.getTypeName(), 
      "Object#" + object.getObjectId(), 
      object.getToString(), 
      object.getReferenceHolders()
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
}
