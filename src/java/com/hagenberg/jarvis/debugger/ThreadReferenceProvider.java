package com.hagenberg.jarvis.debugger;

import java.util.List;

import com.sun.jdi.ThreadReference;

public interface ThreadReferenceProvider {
  List<ThreadReference> getThreads();
}
