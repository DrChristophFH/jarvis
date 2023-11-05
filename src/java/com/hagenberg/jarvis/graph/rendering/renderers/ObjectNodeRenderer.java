package com.hagenberg.jarvis.graph.rendering.renderers;

import com.hagenberg.imgui.Bounder;
import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Draw;
import com.hagenberg.imgui.Vec2;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

import imgui.ImGui;
import imgui.flag.ImGuiButtonFlags;
import imgui.flag.ImGuiMouseButton;

public class ObjectNodeRenderer {
  public void render(Draw draw, ObjectGNode node) {
    Vec2 origin = new Vec2();

    String text = node.getType() + " Object#" + node.getId();

    Vec2 textOffset = new Vec2(5, 5);
    Vec2 textSize = new Vec2(ImGui.calcTextSize(text));
    Vec2 nameBoxBR = new Vec2(textOffset).scale(2).add(textSize);

    if (ImGui.isItemActive() && ImGui.isMouseClicked(1)) {
      System.out.println("right click on canvas")
    }

    draw.addRect(origin, nameBoxBR, Colors.TextColor);
    draw.addText(textOffset, Colors.TextColor, text, 13);

    draw.pushOffset(new Vec2(0, nameBoxBR.y));

    draw.pushBounder(new Bounder());

    for (MemberGVariable member : node.getMembers()) {
      RendererRegistry.getInstance().getVariableRenderer(member).render(draw, member);
    }
    Vec2 memberBoxBR = draw.popBounder();

    draw.addRect(origin, memberBoxBR, Colors.TextColor);

    draw.popOffset();
  }
}
