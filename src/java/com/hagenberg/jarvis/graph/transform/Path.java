package com.hagenberg.jarvis.graph.transform;

import java.util.List;

import com.hagenberg.jarvis.models.entities.wrappers.JMember;
import com.hagenberg.jarvis.models.entities.wrappers.JObjectReference;
import com.hagenberg.jarvis.models.entities.wrappers.JValue;

public class Path {

  private List<String> path;

  public Path(List<String> path) {
    this.path = path;
  }

  public Path(String path) {
    setPath(path);
  }

  public void setPath(List<String> path) {
    this.path = path;
  }

  public void setPath(String path) {
    this.path = List.of(path.split("\\."));
  }

  /**
   * Resolves the path starting from the given object.
   * @param startObj the object to start from
   * @return the node at the end of the path or null if the path could not be resolved
   */
  public JMember resolve(JObjectReference startObj) {
    JMember currentMember = null;
    JValue current = startObj;
    for (String member : path) {
      if (current instanceof JObjectReference obj) {
        currentMember = obj.getMember(member);
        if (currentMember == null) {
          return null;
        }
        current = currentMember.value();
      } else {
        return null;
      }
    }
    return currentMember;
  }

  @Override
  public String toString() {
    if (path.isEmpty()) {
      return "<empty path>";
    }
    StringBuilder builder = new StringBuilder();
    for (String member : path) {
      builder.append(member);
      builder.append(".");
    }
    return builder.substring(0, builder.length() - 1);
  }
}
