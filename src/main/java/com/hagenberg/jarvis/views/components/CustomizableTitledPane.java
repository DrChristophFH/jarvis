package com.hagenberg.jarvis.views.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * A customizable titled pane that allows the user to set the header and content nodes.
 * The content node is hidden by default and can be toggled by clicking the toggle button.
 * @implNote The internal structure of this component is as follows:
 * <pre>
 *     VBox [class="customizable-titled-pane-container"]
 *     └── HBox [class="customizable-titled-pane-header"]
 *     │   └── Button
 *     │   └── (Header Node)
 *     └── (Content Node)
 * </pre>
 */
public class CustomizableTitledPane extends Region {
    private final VBox container = new VBox();
    private final HBox headerContainer = new HBox();
    private final Button toggleButton = new Button("►");
    private Node content;

    private final BooleanProperty expandedProperty = new SimpleBooleanProperty(false);

    public CustomizableTitledPane() {
        getStyleClass().add("customizable-titled-pane");
        getChildren().add(container);

        container.getChildren().add(headerContainer);
        container.getStyleClass().add("customizable-titled-pane-container");

        headerContainer.getChildren().add(toggleButton);
        headerContainer.getStyleClass().add("customizable-titled-pane-header");

        expandedProperty.addListener((observable, oldValue, newValue) -> {
            toggleButton.setText(newValue ? "▼" : "►");
        });

        toggleButton.setOnAction(event -> toggle());
    }

    public HBox header() {
        return headerContainer;
    }

    public void setContent(Node node) {
        content = node;
        container.getChildren().add(node);
        content.visibleProperty().bind(expandedProperty);
        content.managedProperty().bind(expandedProperty);
    }

    public boolean isExpanded() {
        return expandedProperty.get();
    }

    public BooleanProperty expandedProperty() {
        return expandedProperty;
    }

    public void setExpanded(boolean expanded) {
        expandedProperty.set(expanded);
    }

    public void toggle() {
        setExpanded(!isExpanded());
    }
}
