package com.hagenberg.jarvis.graph.transform.simple;

import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.render.attributes.DefaultMemberAttribute;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.graph.transform.AttributeTransformer;
import com.hagenberg.jarvis.graph.transform.IdProvider;
import com.hagenberg.jarvis.graph.transform.LinkRegisterCallback;
import com.hagenberg.jarvis.models.entities.graph.ContentGVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.models.entities.wrappers.JPrimitiveValue;
import com.hagenberg.jarvis.models.entities.wrappers.JValue;

public class SimpleContentTransformer extends AttributeTransformer<ContentGVariable> {

  @Override
  public Attribute transform(ContentGVariable content, IdProvider idProvider, Node parent, LinkRegisterCallback linkRegisterCallback) {
    JValue node = content.getNode();
    boolean isPrimitive = node instanceof JPrimitiveValue;
    String value;

    if (isPrimitive) {
      value = content.getNode().getToString();
    } else if (content.getNode() instanceof JObjectReference obj) {
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

    if (node != null && node instanceof JObjectReference obj) {
      linkRegisterCallback.registerLink(parent, attribute.getAttId(), obj);
    }
    
    return attribute;
  }
}
