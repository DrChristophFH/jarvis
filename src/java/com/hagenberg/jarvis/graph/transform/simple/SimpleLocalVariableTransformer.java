package com.hagenberg.jarvis.graph.transform.simple;

import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.render.attributes.DefaultLocalVariableAttribute;
import com.hagenberg.jarvis.graph.render.nodes.DefaultLocalVariableNode;
import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.graph.transform.IdProvider;
import com.hagenberg.jarvis.graph.transform.LinkRegisterCallback;
import com.hagenberg.jarvis.graph.transform.NodeTransformer;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;

public class SimpleLocalVariableTransformer extends NodeTransformer<LocalGVariable> {

  public SimpleLocalVariableTransformer() {
    name = "Simple Local Variable Renderer";
  }

  @Override
  public Node transform(LocalGVariable localVariable, IdProvider idProvider, LinkRegisterCallback linkRegisterCallback) {
    Node node = new DefaultLocalVariableNode(
      idProvider.next(),
      localVariable.getName()
    );

    String value = "null";

    if (localVariable.getNode() instanceof PrimitiveGNode primitve) {
      value = primitve.getToString();
    } else if (localVariable.getNode() instanceof ObjectGNode object) {
      value = "Object#" + object.getObjectId();
    }

    Attribute att = new DefaultLocalVariableAttribute(
      idProvider.next(),
      node,
      true,
      localVariable.getStaticTypeName(),
      localVariable.getName(),
      value
    );
    node.addAttribute(att);

    return node;
  }
}
