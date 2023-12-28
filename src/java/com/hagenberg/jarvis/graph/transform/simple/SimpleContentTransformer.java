package com.hagenberg.jarvis.graph.transform.simple;

import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.render.attributes.DefaultMemberAttribute;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.graph.transform.AttributeTransformer;
import com.hagenberg.jarvis.graph.transform.IdProvider;
import com.hagenberg.jarvis.graph.transform.LinkRegisterCallback;
import com.hagenberg.jarvis.models.entities.wrappers.JArrayReference;
import com.hagenberg.jarvis.models.entities.wrappers.JContent;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.models.entities.wrappers.JPrimitiveValue;
import com.hagenberg.jarvis.models.entities.wrappers.JValue;

public class SimpleContentTransformer extends AttributeTransformer<JArrayReference, JContent> {

  @Override
  public Attribute transform(JArrayReference array, JContent content, IdProvider idProvider, Node parent, LinkRegisterCallback linkRegisterCallback) {
    JValue node = content.value();
    boolean isPrimitive = node instanceof JPrimitiveValue;
    String value;

    if (isPrimitive) {
      value = content.value().getToString();
    } else if (content.value() instanceof JObjectReference obj) {
      value = "Reference to Object#" + obj.getObjectId();
    } else {
      value = "null";
    }
    
    Attribute attribute = new DefaultMemberAttribute(
      idProvider.next(), 
      parent,
      isPrimitive,
      "(public)",
      array.getArrayContentType(),
      content.name(),
      value
    );

    if (node != null && node instanceof JObjectReference obj) {
      linkRegisterCallback.registerLink(parent, attribute.getAttId(), obj);
    }
    
    return attribute;
  }
}
