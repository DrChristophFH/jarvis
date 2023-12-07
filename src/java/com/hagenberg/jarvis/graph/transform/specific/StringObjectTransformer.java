package com.hagenberg.jarvis.graph.transform.specific;

import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.graph.render.nodes.StringNode;
import com.hagenberg.jarvis.graph.transform.IdProvider;
import com.hagenberg.jarvis.graph.transform.LinkRegisterCallback;
import com.hagenberg.jarvis.graph.transform.NodeTransformer;
import com.hagenberg.jarvis.graph.transform.TransformerContextMenu;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;

public class StringObjectTransformer extends NodeTransformer<JObjectReference> {

  private final TransformerContextMenu transformerContextMenu;

  public StringObjectTransformer(TransformerContextMenu transformerContextMenu) {
    name = "String Simplified Renderer";
    this.transformerContextMenu = transformerContextMenu;
  }

  @Override
  public Node transform(JObjectReference object, IdProvider idProvider, LinkRegisterCallback linkRegisterCallback) {
    return new StringNode(
      idProvider.next(), 
      object.getTypeName(), 
      "Object#" + object.getObjectId(), 
      object.getToString(), 
      object.getReferenceHolders(),
      transformerContextMenu
    );
  }
}