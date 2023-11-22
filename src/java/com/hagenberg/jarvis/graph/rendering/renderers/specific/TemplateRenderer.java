package com.hagenberg.jarvis.graph.rendering.renderers.specific;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.Vec2;
import com.hagenberg.jarvis.graph.IdProvider;
import com.hagenberg.jarvis.graph.LayoutNode;
import com.hagenberg.jarvis.graph.rendering.Path;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.graph.rendering.renderers.Renderer;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.views.ObjectGraph;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;

public class TemplateRenderer extends Renderer<ObjectGNode> {

  private List<Path> paths = new ArrayList<>();
  private IdProvider ids;

  public TemplateRenderer(String name, RendererRegistry registry, IdProvider ids) {
    super(ObjectGNode.class, name, registry);
    this.ids = ids;
  }

  public List<Path> getPaths() {
    return paths;
  }

  @Override
  public void render(ObjectGNode node, int id, ObjectGraph graph) {
    ImNodes.beginNode(id);

    graph.registerNodeForLayout(node.getLayoutNode());

    ImNodes.setNodeDraggable(id, true);
    Vec2 position = node.getLayoutNode().getPosition();
    ImNodes.setNodeGridSpacePos(id, position.x, position.y);
    node.getLayoutNode().setLength((int) ImNodes.getNodeDimensionsX(id));

    ImNodes.beginNodeTitleBar();
    Snippets.drawTypeWithTooltip(node.getTypeName(), tooltip);
    ImGui.sameLine();
    ImGui.text("Object#" + node.getObjectId());
    ImGui.sameLine();
    ImGui.textColored(Colors.Attention, "[" + name + "]");
    ImNodes.endNodeTitleBar();

    // Reference attribute has node id
    ImNodes.beginInputAttribute(id);
    ImGui.text(node.getReferenceHolders().size() + " references");
    tooltip.show(() -> {
      for (int i = 0; i < node.getReferenceHolders().size(); i++) {
        ImGui.text("-> " + node.getReferenceHolders().get(i).getName());
      }
    });
    ImNodes.endInputAttribute();

    showToString(node, id);

    for (Path path : paths) {
      MemberGVariable resolved = path.resolve(node);
      if (resolved != null) {
        registry.getMemberRenderer(resolved).render(resolved, ids.nextId(), graph);
        
      } else {
        ImGui.textColored(Colors.Error, "Could not resolve path: " + path);
      }
    }

    Snippets.DisplayLayoutNodeDebug(node.getLayoutNode());

    ImNodes.endNode();
  }

  private void showToString(ObjectGNode node, int id) {
    ImGui.textColored(Colors.Type, "toString():");
    ImGui.sameLine();
    ImGui.pushTextWrapPos(ImGui.getCursorPosX() + ImNodes.getNodeDimensionsX(id)); // TODO: make word wrap configurable
    ImGui.text(node.getToString());
    ImGui.popTextWrapPos();
  }
}
