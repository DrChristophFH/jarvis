package com.hagenberg.jarvis.views.interfaces;

import javafx.scene.Node;

public interface ContainerActions {
    void hide(Node child);
    void show(Node child);
    void toggle(Node child);
}
