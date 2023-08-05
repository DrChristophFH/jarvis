package com.hagenberg.jarvis.views.components;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AuxiliaryPane extends VBox {
    private final HBox header = new HBox();
    private final ScrollPane content = new ScrollPane();

    public AuxiliaryPane() {
        super();
        initialize();
    }

    private void initialize() {
        buildHeader();
    }

    private void buildHeader() {
        header.getStyleClass().add("auxiliary-pane-header");
        header.
    }
}
