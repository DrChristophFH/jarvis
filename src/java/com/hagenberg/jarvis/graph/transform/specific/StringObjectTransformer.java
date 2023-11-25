package com.hagenberg.jarvis.graph.transform.specific;

import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.graph.render.nodes.StringNode;
import com.hagenberg.jarvis.graph.transform.IdProvider;
import com.hagenberg.jarvis.graph.transform.LinkRegisterCallback;
import com.hagenberg.jarvis.graph.transform.NodeTransformer;
import com.hagenberg.jarvis.graph.transform.TransformerRegistry;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.util.Procedure;

public class StringObjectTransformer extends NodeTransformer<ObjectGNode> {

  private final TransformerRegistry registry;
  private final Procedure triggerRetransform;

  public StringObjectTransformer(TransformerRegistry registry, Procedure triggerRetransform) {
    name = "String Simplified Renderer";
    this.registry = registry;
    this.triggerRetransform = triggerRetransform;
  }

  @Override
  public Node transform(ObjectGNode object, IdProvider idProvider, LinkRegisterCallback linkRegisterCallback) {
    return new StringNode(
      idProvider.next(), 
      object.getTypeName(), 
      "Object#" + object.getObjectId(), 
      object.getToString(), 
      object.getReferenceHolders(),
      registry,
      object,
      triggerRetransform
    );
  }
}
