package com.hagenberg.jarvis.graph.rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hagenberg.jarvis.graph.rendering.renderers.Renderer;
import com.hagenberg.jarvis.graph.rendering.renderers.simple.SimpleContentRenderer;
import com.hagenberg.jarvis.graph.rendering.renderers.simple.SimpleFieldRenderer;
import com.hagenberg.jarvis.graph.rendering.renderers.simple.SimpleLocalVariableRenderer;
import com.hagenberg.jarvis.graph.rendering.renderers.simple.SimpleObjectNodeRenderer;
import com.hagenberg.jarvis.graph.rendering.renderers.simple.SimplePrimitiveRenderer;
import com.hagenberg.jarvis.graph.rendering.renderers.specific.BinaryRenderer;
import com.hagenberg.jarvis.models.entities.graph.ContentGVariable;
import com.hagenberg.jarvis.models.entities.graph.GNode;
import com.hagenberg.jarvis.models.entities.graph.GVariable;
import com.hagenberg.jarvis.models.entities.graph.LocalGVariable;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;
import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Type;

public class RendererRegistry {

  // all renderers
  private List<Renderer<?>> renderers = new ArrayList<>();

  // renderers set to be used for certain types, objs, locals, or members by the user
  private Map<Type, Renderer<GNode>> typeRenderers = new HashMap<>();
  private Map<Long, Renderer<ObjectGNode>> objRenderers = new HashMap<>();
  private Map<LocalVariable, Renderer<PrimitiveGNode>> localVarRenderers = new HashMap<>();
  private Map<Field, Renderer<PrimitiveGNode>> memVarRenderers = new HashMap<>();

  // default renderers
  private Renderer<LocalGVariable> defaultLocalVarRenderer = new SimpleLocalVariableRenderer("Default Local Variable Renderer", this);
  private Renderer<MemberGVariable> defaultMemVarRenderer = new SimpleFieldRenderer("Default Member Variable Renderer", this);
  private Renderer<ContentGVariable> defaultContentRenderer = new SimpleContentRenderer("Default Content Renderer", this);
  private Renderer<PrimitiveGNode> defaultPrimRenderer = new SimplePrimitiveRenderer("Default Primitive Renderer", this);
  private Renderer<ObjectGNode> defaultObjRenderer = new SimpleObjectNodeRenderer("Default Object Renderer", this);

  public RendererRegistry() {
    // add default renderers
    renderers.add(defaultLocalVarRenderer);
    renderers.add(defaultMemVarRenderer);
    renderers.add(defaultContentRenderer);
    renderers.add(defaultPrimRenderer);
    renderers.add(defaultObjRenderer);

    // add specific renderers
    renderers.add(new BinaryRenderer("Binary Renderer", this));
  }

  public Renderer<LocalGVariable> getLocalRenderer(LocalGVariable localVar) {
    return defaultLocalVarRenderer;
  }

  public Renderer<MemberGVariable> getMemberRenderer(MemberGVariable member) {
    return defaultMemVarRenderer;
  }

  public Renderer<ContentGVariable> getContentRenderer(ContentGVariable content) {
    return defaultContentRenderer;
  }

  public Renderer<ObjectGNode> getObjectRenderer(ObjectGNode obj) {
    Renderer<ObjectGNode> renderer = objRenderers.get(obj.getObjectId()); // object specific

    if (renderer == null) {
      // renderer = typeRenderers.get(obj.getStaticType()); // dynamic type
      if (renderer == null) {
        renderer = defaultObjRenderer; // default
      }
    }

    return renderer;
  }

  public Renderer<PrimitiveGNode> getPrimitiveRenderer(GVariable variable) {
    Renderer<PrimitiveGNode> renderer = null;
    // renderer = typeRenderers.get(member.getStaticType()); // dynamic type
    if (renderer == null) {
      renderer = defaultPrimRenderer; // default
    }
    return renderer;
  }

  public Renderer<PrimitiveGNode> getPrimitiveRenderer(MemberGVariable member) {
    Renderer<PrimitiveGNode> renderer = memVarRenderers.get(member.getField()); // field specific

    if (renderer == null) {
      // renderer = typeRenderers.get(member.getStaticType()); // dynamic type
      if (renderer == null) {
        renderer = defaultPrimRenderer; // default
      }
    }

    return renderer;
  }

  public Renderer<PrimitiveGNode> getPrimitiveRenderer(LocalGVariable localVar) {
    Renderer<PrimitiveGNode> renderer = localVarRenderers.get(localVar.getLocalVariable()); // local var specific

    if (renderer == null) {
      // renderer = typeRenderers.get(localVar.type()); // dynamic type
      if (renderer == null) {
        renderer = defaultPrimRenderer; // default
      }
    }

    return renderer;
  }

  public <T> List<Renderer<T>> getApplicableRenderers(T node) {
    List<Renderer<T>> applicableRenderers = new ArrayList<>();
    for (Renderer<?> renderer : renderers) {
      if (renderer.canHandle(node)) {
        @SuppressWarnings("unchecked")
        Renderer<T> castedRenderer = (Renderer<T>) renderer;
        if (castedRenderer.isApplicable(node)) {
          applicableRenderers.add(castedRenderer);
        }
      }
    }
    return applicableRenderers;
  }

  public void setFieldRenderer(Field field, Renderer<PrimitiveGNode> renderer) {
    memVarRenderers.put(field, renderer);
  }
}
