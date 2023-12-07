package com.hagenberg.jarvis.graph.transform.simple;

import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.render.attributes.DefaultLocalVariableAttribute;
import com.hagenberg.jarvis.graph.render.nodes.DefaultLocalVariableNode;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.graph.transform.IdProvider;
import com.hagenberg.jarvis.graph.transform.LinkRegisterCallback;
import com.hagenberg.jarvis.graph.transform.NodeTransformer;
import com.hagenberg.jarvis.models.entities.wrappers.JLocalVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.models.entities.wrappers.JPrimitiveValue;

public class SimpleLocalVariableTransformer extends NodeTransformer<JLocalVariable> {

  public SimpleLocalVariableTransformer() {
    name = "Simple Local Variable Renderer";
  }

  @Override
  public Node transform(JLocalVariable localVariable, IdProvider idProvider, LinkRegisterCallback linkRegisterCallback) {
    Node node = new DefaultLocalVariableNode(
      idProvider.next(),
      localVariable.name()
    );

    String value = "null";
    int attId = idProvider.next();

    if (localVariable.value() instanceof JPrimitiveValue primitve) {
      value = primitve.getToString();
    } else if (localVariable.value() instanceof JObjectReference object) {
      value = "Object#" + object.getObjectId();
      linkRegisterCallback.registerLink(node, attId, object);
    }

    Attribute att = new DefaultLocalVariableAttribute(
      attId,
      node,
      localVariable.value() instanceof JPrimitiveValue,
      localVariable.getStaticTypeName(),
      localVariable.name(),
      value
    );
    node.addAttribute(att);

    return node;
  }
}
