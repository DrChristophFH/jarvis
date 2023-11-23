package com.hagenberg.jarvis.graph.transform.simple;

import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.render.attributes.DefaultMemberAttribute;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.graph.transform.AttributeTransformer;
import com.hagenberg.jarvis.graph.transform.IdProvider;
import com.hagenberg.jarvis.graph.transform.LinkRegisterCallback;
import com.hagenberg.jarvis.models.entities.graph.ContentGVariable;
import com.hagenberg.jarvis.models.entities.graph.GNode;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;

public class SimpleContentTransformer extends AttributeTransformer<ContentGVariable> {

  @Override
  public Attribute transform(ContentGVariable content, IdProvider idProvider, Node parent, LinkRegisterCallback linkRegisterCallback) {
    GNode node = content.getNode();
    boolean isPrimitive = node instanceof PrimitiveGNode;
    String value;

    if (isPrimitive) {
      value = content.getNode().getToString();
    } else if (content.getNode() instanceof ObjectGNode obj) {
      value = "Reference to Object#" + obj.getObjectId();
    } else {
      value = "null";
    }
    
    Attribute attribute = new DefaultMemberAttribute(
      idProvider.next(), 
      parent,
      isPrimitive,
      "(public)",
      content.getStaticTypeName(),
      content.getName(),
      value
    );

    if (node != null && node instanceof ObjectGNode obj) {
      linkRegisterCallback.registerLink(parent, attribute.getId(), obj);
    }
    
    return attribute;
  }
}
