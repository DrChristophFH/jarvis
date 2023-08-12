package com.hagenberg.jarvis.views.components;

import com.hagenberg.jarvis.models.WindowVisibilityModel;
import com.hagenberg.jarvis.util.ServiceProvider;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.awt.*;

public class WindowMenuItem extends VBox {

    private final WindowVisibilityModel visibilityModel = ServiceProvider.getInstance().getDependency(WindowVisibilityModel.class);

    public WindowMenuItem(AuxiliaryPane pane, SVGPath icon, boolean bottomUp) {
        super();
        StringProperty title = pane.getTitle();
        this.getStyleClass().add("window-menu-item");

        Label titleLabel = new Label();
        titleLabel.textProperty().bind(title);
        titleLabel.getStyleClass().add("window-menu-item-title");

        // the group is needed for the label rotation to work
        // if no group is used, the label won't get enough space and clip weirdly
        Group labelContainer = new Group();
        labelContainer.getChildren().add(titleLabel);

        icon.getStyleClass().add("icon");
        this.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            BooleanProperty visibilityState = visibilityModel.getVisibilityState(pane);
            visibilityState.setValue(!visibilityState.getValue()); // toggle visibility
        });

        if (bottomUp) {
            titleLabel.setRotate(90);
            this.getChildren().addAll(icon, labelContainer);
        } else {
            titleLabel.setRotate(-90);
            this.getChildren().addAll(labelContainer, icon);
        }
    }
}
