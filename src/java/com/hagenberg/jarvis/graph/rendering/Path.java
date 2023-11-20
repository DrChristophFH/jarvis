package com.hagenberg.jarvis.graph.rendering;

import java.lang.reflect.Member;
import java.util.List;

import com.hagenberg.jarvis.models.entities.graph.GNode;
import com.hagenberg.jarvis.models.entities.graph.GVariable;
import com.hagenberg.jarvis.models.entities.graph.MemberGVariable;
import com.hagenberg.jarvis.models.entities.graph.ObjectGNode;

public class Path {

  private List<String> path;

  public Path(List<String> path) {
    this.path = path;
  }

  /**
   * Resolves the path starting from the given object.
   * @param startObj the object to start from
   * @return the node at the end of the path or null if the path could not be resolved
   */
  public MemberGVariable resolve(ObjectGNode startObj) {
    MemberGVariable currentMember = null;
    GNode current = startObj;
    for (String member : path) {
      if (current instanceof ObjectGNode obj) {
        currentMember = obj.getMember(member);
        if (currentMember == null) {
          return null;
        }
        current = currentMember.getNode();
      } else {
        return null;
      }
    }
    return currentMember;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (String member : path) {
      builder.append(member);
      builder.append(".");
    }
    return builder.substring(0, builder.length() - 1);
  }
}
