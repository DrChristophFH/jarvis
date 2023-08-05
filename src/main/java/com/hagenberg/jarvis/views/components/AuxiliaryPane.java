package com.hagenberg.jarvis.views.components;

import com.hagenberg.jarvis.util.SVGManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class AuxiliaryPane extends VBox {
    private final HBox header = new HBox();
    private final ScrollPane content = new ScrollPane();
    private final StringProperty title = new SimpleStringProperty();

    public AuxiliaryPane(String title) {
        super();
        this.title.setValue(title);
        initialize();
    }

    private void initialize() {
        buildHeader();
    }

    private void buildHeader() {
        header.getStyleClass().add("auxiliary-pane-header");

        Label titleLabel = new Label();
        titleLabel.textProperty().bind(title);
        titleLabel.getStyleClass().add("auxiliary-pane-title");
        header.getChildren().add(titleLabel);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        header.getChildren().add(spacer);

        Button minimizeButton = new Button();
        minimizeButton.getStyleClass().add("icon-button");
        SVGPath minimizeIcon = SVGManager.getInstance().getSVG("/icons/minimize.svg");
        minimizeIcon.getStyleClass().add("icon");
        minimizeButton.setGraphic(minimizeIcon);
        // TODO: minimizeButton.setOnAction(e -> minimize());
        header.getChildren().add(minimizeButton);

        getChildren().add(header);
    }
}
