package com.hagenberg.jarvis.graph.transform;

import com.hagenberg.jarvis.graph.transform.simple.SimpleLocalVariableTransformer;
import com.hagenberg.jarvis.graph.transform.simple.SimpleMemberTransformer;
import com.hagenberg.jarvis.graph.transform.simple.SimpleContentTransformer;
import com.hagenberg.jarvis.graph.transform.simple.SimpleObjectTransformer;
import com.hagenberg.jarvis.models.entities.graph.ContentGVariable;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

public class TransformerRegistry {

  // default transformers
  private NodeTransformer<ObjectGNode> defaultObjectRenderer = new SimpleObjectTransformer(this);
  private NodeTransformer<LocalGVariable> defaultLocalVarRenderer = new SimpleLocalVariableTransformer();
  private AttributeTransformer<MemberGVariable> defaultMemberRenderer = new SimpleMemberTransformer();
  private AttributeTransformer<ContentGVariable> defaultContentRenderer = new SimpleContentTransformer();

  public NodeTransformer<LocalGVariable> getLocalVarTransformer(LocalGVariable localVar) {
    return defaultLocalVarRenderer;
  }

  public NodeTransformer<ObjectGNode> getObjectTransformer(ObjectGNode object) {
    return defaultObjectRenderer;
  }

  public AttributeTransformer<MemberGVariable> getMemberTransformer(MemberGVariable member) {
    return defaultMemberRenderer;
  }

  public AttributeTransformer<ContentGVariable> getContentTransformer(ContentGVariable content) {
    return defaultContentRenderer;
  }
}
