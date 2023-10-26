package com.hagenberg.jarvis.debugger;

import java.util.List;

public interface BreakPointProvider {
  public List<Integer> getBreakPoints(String className);
}
