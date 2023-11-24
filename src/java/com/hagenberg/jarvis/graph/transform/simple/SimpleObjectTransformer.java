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
import com.hagenberg.jarvis.util.Procedure;

public class SimpleObjectTransformer extends NodeTransformer<ObjectGNode> {

  private final TransformerRegistry registry;
  private final Procedure triggerRetransform;

  public SimpleObjectTransformer(TransformerRegistry registry, Procedure triggerRetransform) {
    name = "Simple Object Renderer";
    this.registry = registry;
    this.triggerRetransform = triggerRetransform;
  }

  @Override
  public DefaultObjectNode transform(ObjectGNode object, IdProvider idProvider, LinkRegisterCallback linkRegisterCallback) {
    DefaultObjectNode objNode = new DefaultObjectNode(
      idProvider.next(), 
      object.getTypeName(), 
      "Object#" + object.getObjectId(), 
      object.getToString(), 
      object.getReferenceHolders(),
      registry,
      object,
      triggerRetransform
    );

    for (MemberGVariable member : object.getMembers()) {
      Attribute att = registry.getMemberTransformer(member).transform(member, idProvider, objNode, linkRegisterCallback);
      objNode.addAttribute(att);
    }

    if (object instanceof ArrayGNode arrayGNode) {
      for (ContentGVariable content : arrayGNode.getContent()) {
        Attribute att = registry.getContentTransformer(content).transform(content, idProvider, objNode, linkRegisterCallback);
        objNode.addAttribute(att);
      }
    }

    return objNode;
  }
}
