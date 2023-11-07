package com.hagenberg.jarvis.graph.rendering.renderers;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.jarvis.graph.rendering.Link;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;
import com.hagenberg.jarvis.util.TypeFormatter;

import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.flag.ImNodesColorStyle;

public class MemberVariableRenderer {
  public void render(MemberGVariable var, int attId, List<Link> links) {
    boolean isPrimitive = var.getNode() instanceof PrimitiveGNode;
    
    if (isPrimitive) {
      ImNodes.pushColorStyle(ImNodesColorStyle.Pin, Colors.Invisible);
      ImNodes.pushColorStyle(ImNodesColorStyle.PinHovered, Colors.Invisible);
    }

    ImNodes.beginOutputAttribute(attId);
    ImGui.textColored(Colors.AccessModifier, var.getAccessModifier().toString());
    ImGui.sameLine();
    TypeFormatter.drawTypeWithTooltip(var.getStaticType());
    ImGui.sameLine();
    ImGui.text(var.getName());
    
    if (isPrimitive) {
      PrimitiveGNode prim = (PrimitiveGNode) var.getNode();

      ImGui.sameLine();
      ImGui.text(" = " + prim.getPrimitiveValue().toString());
      
      ImNodes.popColorStyle();
      ImNodes.popColorStyle();
    } else if (var.getNode() instanceof ObjectGNode obj) {
      links.add(new Link(attId, obj.getNodeId()));
    }

    ImNodes.endOutputAttribute();
  }
}
