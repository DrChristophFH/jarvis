package com.hagenberg.jarvis.models;

public class InteractionState {
  private long selectedObjectId = -1;

  public long getSelectedObjectId() {
    return selectedObjectId;
  }

  public void setSelectedObjectId(long selectedObjectId) {
    this.selectedObjectId = selectedObjectId;
  }
}
