package com.hagenberg.jarvis.graph.transform.simple;

import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.render.attributes.DefaultMemberAttribute;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.graph.transform.AttributeTransformer;
import com.hagenberg.jarvis.graph.transform.IdProvider;
import com.hagenberg.jarvis.graph.transform.LinkRegisterCallback;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.models.entities.wrappers.JPrimitiveValue;
import com.hagenberg.jarvis.models.entities.wrappers.JValue;

public class SimpleMemberTransformer extends AttributeTransformer<JValue> {

  @Override
  public Attribute transform(MemberGVariable member, IdProvider idProvider, Node parent, LinkRegisterCallback linkRegisterCallback) {
    JValue node = member.value();
    boolean isPrimitive = node instanceof JPrimitiveValue;
    String value;

    if (isPrimitive) {
      value = member.value().getToString();
    } else if (member.value() instanceof JObjectReference obj) {
      value = "Reference to Object#" + obj.getObjectId();
    } else {
      value = "null";
    }
    
    Attribute attribute = new DefaultMemberAttribute(
      idProvider.next(), 
      parent,
      isPrimitive,
      member.getAccessModifier().toString(),
      member.getStaticTypeName(),
      member.name(),
      value
    );

    if (node != null && node instanceof JObjectReference obj) {
      linkRegisterCallback.registerLink(parent, attribute.getAttId(), obj);
    }
    
    return attribute;
  }
}
