package com.hagenberg.jarvis.graph.transform;

import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

public interface LinkRegisterCallback {
  void registerLink(Node source, int transformedAttId, ObjectGNode target);
}
