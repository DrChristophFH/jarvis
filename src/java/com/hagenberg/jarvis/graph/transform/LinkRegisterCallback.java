package com.hagenberg.jarvis.graph.transform;

import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

public interface LinkRegisterCallback {
  void registerLink(int transformedAttId, ObjectGNode target);
}
