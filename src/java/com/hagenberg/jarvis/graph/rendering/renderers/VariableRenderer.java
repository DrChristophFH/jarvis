package com.hagenberg.jarvis.graph.rendering.renderers;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Draw;
import com.hagenberg.imgui.Vec2;
import com.hagenberg.jarvis.models.entities.graph.GVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;

import imgui.ImGui;

public class VariableRenderer {
  public void render(Draw draw, GVariable var) {
    Vec2 origin = new Vec2();

    String text = var.getStaticType() + " " + var.getName();

    Vec2 textOffset = new Vec2(5, 5);
    Vec2 textSize = new Vec2(ImGui.calcTextSize(text));
    Vec2 nameBoxBR = new Vec2(textOffset).scale(2).add(textSize);

    draw.addRectB(origin, nameBoxBR, Colors.TextColor);
    draw.addText(textOffset, Colors.TextColor, text, 13);

    draw.pushOffset(new Vec2(nameBoxBR.x, 0));

    if (var.getNode() instanceof PrimitiveGNode prim) { // draw primitive value
      String value = prim.getPrimitiveValue().toString();
      Vec2 valueSize = new Vec2(ImGui.calcTextSize(value));
      Vec2 valueBoxBR = new Vec2(textOffset).scale(2).add(valueSize);
      draw.addRectB(origin, valueBoxBR, Colors.TextColor);
      draw.addText(textOffset, Colors.TextColor, value, 13);
    } else if (var.getNode() instanceof ObjectGNode obj) { // draw line to object
      Vec2 objPos = draw.absolute(obj.getPosition());
      Vec2 refBoxBR = new Vec2(nameBoxBR.y, nameBoxBR.y);
      draw.addRectB(origin, refBoxBR, Colors.TextColor);
      draw.addLine(refBoxBR.scale(0.5f), objPos, Colors.TextColor);
    }
    
    draw.popOffset();
  }
}
