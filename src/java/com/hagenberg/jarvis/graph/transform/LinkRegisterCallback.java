package com.hagenberg.jarvis.graph.transform;

import com.hagenberg.jarvis.graph.render.nodes.Node;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;

public interface LinkRegisterCallback {
  void registerLink(Node source, int transformedAttId, JObjectReference target);
}
