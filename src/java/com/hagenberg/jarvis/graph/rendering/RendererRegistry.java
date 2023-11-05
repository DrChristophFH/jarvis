package com.hagenberg.jarvis.graph.rendering;

import com.hagenberg.jarvis.graph.rendering.renderers.LocalVariableRenderer;
import com.hagenberg.jarvis.graph.rendering.renderers.MemberVariableRenderer;
import com.hagenberg.jarvis.graph.rendering.renderers.ObjectNodeRenderer;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

public class RendererRegistry {

  private static RendererRegistry instance = new RendererRegistry();
  
  private MemberVariableRenderer defaultMemVarRenderer = new MemberVariableRenderer();
  private ObjectNodeRenderer defaultObjRenderer = new ObjectNodeRenderer();
  private LocalVariableRenderer defaultLocalVarRenderer = new LocalVariableRenderer();

  private RendererRegistry() {
  }

  public static RendererRegistry getInstance() {
    return instance;
  }

  public LocalVariableRenderer getLocalVariableRenderer(LocalGVariable var) {
    return defaultLocalVarRenderer;
  }

  public MemberVariableRenderer getMemberVariableRenderer(MemberGVariable var) {
    return defaultMemVarRenderer;
  }

  public ObjectNodeRenderer getObjectRenderer(ObjectGNode obj) {
    return defaultObjRenderer;
  }
}
