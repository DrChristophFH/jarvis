package com.hagenberg.jarvis.graph.transform.simple;

import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.render.nodes.DefaultObjectNode;
import com.hagenberg.jarvis.graph.transform.IdProvider;
import com.hagenberg.jarvis.graph.transform.LinkRegisterCallback;
import com.hagenberg.jarvis.graph.transform.NodeTransformer;
import com.hagenberg.jarvis.graph.transform.TransformerRegistry;
import com.hagenberg.jarvis.models.entities.graph.ArrayGNode;
import com.hagenberg.jarvis.models.entities.graph.ContentGVariable;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

public class SimpleObjectTransformer extends NodeTransformer<ObjectGNode> {

  private final TransformerRegistry registry;

  public SimpleObjectTransformer(TransformerRegistry registry) {
    name = "Simple Object Renderer";
    this.registry = registry;
  }

  @Override
  public DefaultObjectNode transform(ObjectGNode node, IdProvider idProvider, LinkRegisterCallback linkRegisterCallback) {
    DefaultObjectNode objNode = new DefaultObjectNode(
      idProvider.next(), 
      node.getTypeName(), 
      "Object#" + node.getObjectId(), 
      node.getToString(), 
      node.getReferenceHolders()
    );

    for (MemberGVariable member : node.getMembers()) {
      Attribute att = registry.getMemberTransformer(member).transform(member, idProvider, objNode, linkRegisterCallback);
      objNode.addAttribute(att);
    }

    if (node instanceof ArrayGNode arrayGNode) {
      for (ContentGVariable content : arrayGNode.getContent()) {
        Attribute att = registry.getContentTransformer(content).transform(content, idProvider, objNode, linkRegisterCallback);
        objNode.addAttribute(att);
      }
    }

    return objNode;
  }
}
