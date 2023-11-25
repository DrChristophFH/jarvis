package com.hagenberg.jarvis.graph.render.nodes;

import java.util.ArrayList;
import java.util.List;

import com.hagenberg.imgui.Vec2;
import com.hagenberg.jarvis.graph.render.attributes.Attribute;
import com.hagenberg.jarvis.graph.transform.NodeTransformer;
import com.hagenberg.jarvis.graph.transform.TransformerRegistry;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.util.Procedure;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;

public abstract class Node {
  protected final int nodeId;

  // positioning
  protected Vec2 position = new Vec2();
  protected Vec2 velocity = new Vec2();
  protected boolean frozen = false;
  protected int length = 0;

  protected final List<Attribute> attributes = new ArrayList<>();

  protected final List<Node> InNeighbors = new ArrayList<>();
  protected final List<Node> OutNeighbors = new ArrayList<>();

  public Node(int nodeId) {
    this.nodeId = nodeId;
  }

  public void render() {
    preNode();
    ImNodes.beginNode(nodeId);
    preHeader();
    beginHeader();
    headerContent();
    endHeader();
    content();
    ImNodes.endNode();
    postNode();
  }
  
  protected void preNode() {
  }

  protected void preHeader() {
    ImNodes.setNodeDraggable(nodeId, true);
    ImNodes.setNodeGridSpacePos(nodeId, position.x, position.y);
    length = (int) ImNodes.getNodeDimensionsX(nodeId);
  }

  protected void beginHeader() {
    ImNodes.beginNodeTitleBar();
  }

  protected abstract void headerContent();

  protected void endHeader() {
    ImNodes.endNodeTitleBar();
  }

  protected abstract void content();

  protected void postNode() {
  }

  @Override
  public int hashCode() {
    return nodeId; // is unique
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Node other = (Node) obj;
    if (nodeId != other.nodeId) return false;
    return true;
  }

  public int getNodeId() {
    return nodeId;
  }

  public Vec2 getPosition() {
    return position;
  }

  public void setPosition(Vec2 position) {
    this.position = position;
  }

  public Vec2 getVelocity() {
    return velocity;
  }

  public void setVelocity(Vec2 velocity) {
    this.velocity = velocity;
  }

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public List<Attribute> getAttributes() {
    return attributes;
  }

  public void addAttribute(Attribute attribute) {
    attributes.add(attribute);
  }

  public List<Node> getInNeighbors() {
    return InNeighbors;
  }

  public List<Node> getOutNeighbors() {
    return OutNeighbors;
  }

  public void clearNeighbors() {
    InNeighbors.clear();
    OutNeighbors.clear();
  }

  /**
   * @param node
   * @param registry
   */
  protected void transformerContextMenu(TransformerRegistry registry, ObjectGNode originNode, Procedure triggerTransform) {
    if (ImGui.isItemHovered() && ImGui.isMouseReleased(ImGuiMouseButton.Right)) {
      ImGui.openPopup("NodeCtx##" + nodeId);
    }

    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5); // NodeEditor somehow overrides this so we have to set it here

    if (ImGui.beginPopup("NodeCtx##" + nodeId)) {
      ImGui.menuItem("Settings", "", false, false);
      if (ImGui.beginMenu("Renderer for this Object")) {
        List<NodeTransformer<ObjectGNode>> transformers = registry.getObjectTransformers();
        NodeTransformer<ObjectGNode> currentTransformer = registry.getSpecificOT(originNode);
        for (NodeTransformer<ObjectGNode> transformer : transformers) {
          boolean selected = transformer == currentTransformer;
          if (ImGui.menuItem(transformer.getName(), "", selected)) {
            registry.setObjectTransformer(originNode, transformer);
            if (!selected) {
              triggerTransform.run();
            }
          }
        }
        if (ImGui.menuItem("[Default]", "", currentTransformer == null)) {
          registry.setObjectTransformer(originNode, null);
        }
        ImGui.endMenu();
      }
      if (ImGui.beginMenu("Renderer for this Type")) {
        List<NodeTransformer<ObjectGNode>> transformers = registry.getObjectTransformers();
        NodeTransformer<ObjectGNode> currentTransformer = registry.getSpecificOTForType(originNode);
        for (NodeTransformer<ObjectGNode> transformer : transformers) {
          boolean selected = transformer == currentTransformer;
          if (ImGui.menuItem(transformer.getName(), "", selected)) {
            registry.setObjectTransformer(originNode.getType(), transformer);
            if (!selected) {
              triggerTransform.run();
            }
          }
        }
        if (ImGui.menuItem("[Default]", "", currentTransformer == null)) {
          registry.setObjectTransformer(originNode.getType(), null);

        }
        ImGui.endMenu();
      }
      ImGui.endPopup();
    }
    ImGui.popStyleVar();
  }
}