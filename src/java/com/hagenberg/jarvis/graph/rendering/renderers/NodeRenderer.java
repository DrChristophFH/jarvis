package com.hagenberg.jarvis.graph.rendering.renderers;

import com.hagenberg.imgui.Draw;
import com.hagenberg.jarvis.graph.LayoutableNode;

public interface NodeRenderer {
    void render(Draw draw, LayoutableNode node);
}
