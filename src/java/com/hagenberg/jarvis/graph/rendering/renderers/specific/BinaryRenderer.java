package com.hagenberg.jarvis.graph.rendering.renderers.specific;

import java.util.List;

import com.hagenberg.imgui.Colors;
import com.hagenberg.jarvis.graph.rendering.Link;
import com.hagenberg.jarvis.graph.rendering.RendererRegistry;
import com.hagenberg.jarvis.graph.rendering.renderers.Renderer;
import com.hagenberg.jarvis.models.entities.graph.PrimitiveGNode;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharValue;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.LongValue;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.ShortValue;

import imgui.ImGui;

public class BinaryRenderer extends Renderer<PrimitiveGNode> {
  public BinaryRenderer(String name, RendererRegistry registry) {
    super(PrimitiveGNode.class, name, registry);
  }

  @Override
  public void render(PrimitiveGNode node, int id, List<Link> links) {
    ImGui.sameLine();

    PrimitiveValue value = node.getPrimitiveValue();
    String binary = "";

    if (value instanceof BooleanValue booleanValue) {
      binary = booleanValue.booleanValue() ? "1" : "0";
    } else if (value instanceof IntegerValue intValue) {
      binary = Integer.toBinaryString(intValue.value());
    } else if (value instanceof ByteValue byteValue) {
      binary = String.format("%8s", Integer.toBinaryString(byteValue.value() & 0xFF)).replace(' ', '0');
    } else if (value instanceof ShortValue shortValue) {
      binary = String.format("%16s", Integer.toBinaryString(shortValue.value() & 0xFFFF)).replace(' ', '0');
    } else if (value instanceof LongValue longValue) {
      binary = Long.toBinaryString(longValue.value());
    } else if (value instanceof CharValue charValue) {
      binary = String.format("%16s", Integer.toBinaryString(charValue.value())).replace(' ', '0');
    } else if (value instanceof FloatValue floatValue) {
      binary = Integer.toBinaryString(Float.floatToIntBits(floatValue.value()));
    } else if (value instanceof DoubleValue doubleValue) {
      binary = Long.toBinaryString(Double.doubleToLongBits(doubleValue.value()));
    }
    ImGui.text("=");
    ImGui.sameLine();
    ImGui.textColored(Colors.Attention, "[binary]");
    ImGui.sameLine();
    ImGui.text(binary);
  }

  @Override
  public boolean isApplicable(PrimitiveGNode node) {
    return true;
  }
}
