package com.hagenberg.jarvis.graph.transform.simple;

import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.render.nodes.DefaultObjectNode;
import com.hagenberg.jarvis.graph.transform.IdProvider;
import com.hagenberg.jarvis.graph.transform.LinkRegisterCallback;
import com.hagenberg.jarvis.graph.transform.NodeTransformer;
import com.hagenberg.jarvis.graph.transform.TransformerContextMenu;
import com.hagenberg.jarvis.graph.transform.TransformerRegistry;
import com.hagenberg.jarvis.models.entities.graph.ContentGVariable;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JArrayReference;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;

public class SimpleObjectTransformer extends NodeTransformer<JObjectReference> {

  private final TransformerRegistry registry;
  private final TransformerContextMenu transformerContextMenu;

  public SimpleObjectTransformer(TransformerRegistry registry, TransformerContextMenu transformerContextMenu) {
    name = "Simple Object Renderer";
    this.registry = registry;
    this.transformerContextMenu = transformerContextMenu;
  }

  @Override
  public DefaultObjectNode transform(JObjectReference object, IdProvider idProvider, LinkRegisterCallback linkRegisterCallback) {
    DefaultObjectNode objNode = new DefaultObjectNode(
      idProvider.next(), 
      object.getTypeName(), 
      "Object#" + object.getObjectId(), 
      object.getToString(), 
      object.getReferenceHolders(),
      transformerContextMenu
    );

    for (MemberGVariable member : object.getMembers()) {
      Attribute att = registry.getMemberTransformer(member).transform(member, idProvider, objNode, linkRegisterCallback);
      objNode.addAttribute(att);
    }

    if (object instanceof JArrayReference arrayGNode) {
      for (ContentGVariable content : arrayGNode.getContent()) {
        Attribute att = registry.getContentTransformer(content).transform(content, idProvider, objNode, linkRegisterCallback);
        objNode.addAttribute(att);
      }
    }

    return objNode;
  }
}
