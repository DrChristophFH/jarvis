package com.hagenberg.jarvis.views.components;

import com.hagenberg.jarvis.models.WindowStateModel;
import com.hagenberg.jarvis.util.SVGManager;
import com.hagenberg.jarvis.util.ServiceProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class AuxiliaryPane extends VBox {
    private final WindowStateModel windowStateModel;
    private final StringProperty title = new SimpleStringProperty();
    private final HBox header = new HBox();
    private final Button minimizeButton = new Button();
    private Node content = new Pane();
    private Node headerRegion = new Pane();

    public AuxiliaryPane(String title) {
        super();
        this.windowStateModel = ServiceProvider.getInstance().getDependency(WindowStateModel.class);
        this.title.setValue(title);
        setupModel();
        buildHeader();
        this.getChildren().addAll(header, content);
    }

    private void setupModel() {
        windowStateModel.addVisibilityState(title.getValue(), this.visibleProperty());
        this.visibleProperty().bind(windowStateModel.getVisibilityState(title.getValue()));
    }

    private void buildHeader() {
        this.getStyleClass().add("auxiliary-pane");
        header.getStyleClass().add("auxiliary-pane-header");

        Label titleLabel = new Label();
        titleLabel.textProperty().bind(title);
        titleLabel.getStyleClass().add("auxiliary-pane-title");
        header.getChildren().add(titleLabel);

        HBox.setHgrow(headerRegion, javafx.scene.layout.Priority.ALWAYS);
        header.getChildren().add(headerRegion);

        minimizeButton.getStyleClass().add("icon-button");
        SVGPath minimizeIcon = SVGManager.getInstance().getSVG("/icons/minimize.svg");
        minimizeIcon.getStyleClass().add("icon");
        minimizeButton.setGraphic(minimizeIcon);
        HBox.setMargin(minimizeButton, new javafx.geometry.Insets(2));
        minimizeButton.setOnAction(event -> windowStateModel.getVisibilityState(title.getValue()).setValue(false));
        header.getChildren().add(minimizeButton);
    }

    public void setContent(Node content) {
        this.content = content;
        this.getChildren().remove(1);
        this.getChildren().add(content);
    }

    public void setHeaderRegion(Node headerRegion) {
        this.headerRegion = headerRegion;
        header.getChildren().remove(1);
        header.getChildren().add(1, headerRegion);
    }
}
