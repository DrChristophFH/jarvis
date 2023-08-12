package com.hagenberg.jarvis.views.components;

import com.hagenberg.jarvis.models.WindowVisibilityModel;
import com.hagenberg.jarvis.util.ServiceProvider;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Split pane container with easy show and hide functionality
 */
public class AuxiliaryContainer extends Region {

    private final WindowVisibilityModel visibilityModel = ServiceProvider.getInstance().getDependency(WindowVisibilityModel.class);
    private final SplitPane splitPane;
    private final List<Node> panes = new ArrayList<>();

    /**
     * Creates a new auxiliary container
     */
    public AuxiliaryContainer() {
        this.splitPane = new SplitPane();
        this.splitPane.getStyleClass().add("auxiliary-container");
        getChildren().add(splitPane);
    }

    /**
     * Creates a new auxiliary container with the given panes
     * @param panes the panes to add to the container
     */
    public AuxiliaryContainer(Node... panes) {
        this();
        addPanes(panes);
    }

    /**
     * Adds the given panes to the container
     * @param panesToAdd the panes to add to the container
     */
    public void addPanes(Node... panesToAdd) {
        for (Node pane : panesToAdd) {
            if (!panes.contains(pane)) {
                panes.add(pane);
                splitPane.getItems().add(pane);
                visibilityModel.getVisibilityState(pane).addListener((observable, oldValue, show) -> {
                    if (show) {
                        show(pane);
                    } else {
                        hide(pane);
                    }
                });
            }
        }
    }

    /**
     * Hides the pane by removing it from the split pane
     * @param pane the pane to hide
     */
    public void hide(Node pane) {
        splitPane.getItems().remove(pane);
    }

    /**
     * Shows the pane by adding it to the split pane.
     * If the pane is not in the container, it will be added to the container.
     * @param pane the pane to display
     */
    public void show(Node pane) {
        if (!splitPane.getItems().contains(pane)) {
            int originalIndex = panes.indexOf(pane);
            if (originalIndex != -1) {
                splitPane.getItems().add(mapIndex(originalIndex), pane);
            } else {
                // If the pane was never added to the container, add it to the end
                addPanes(pane);
            }
        }
    }

    /**
     * @param originalIndex the index of the pane in the original list
     * @return the index in the split pane to insert the pane at
     */
    private int mapIndex(int originalIndex) {
        for (int i = 0; i < splitPane.getItems().size(); i++) {
            Node pane = splitPane.getItems().get(i);
            if (panes.indexOf(pane) > originalIndex) {
                return i;
            }
        }
        return splitPane.getItems().size();
    }

    /**
     * Toggles the pane by hiding it if it is visible and showing it if it is hidden
     * @param child the pane to toggle
     */
    public void toggle(Node child) {
        if (isPaneHidden(child)) {
            show(child);
        } else {
            hide(child);
        }
    }

    /**
     * Sets the orientation of the split pane
     * @param orientation the orientation to set
     */
    public void setOrientation(Orientation orientation) {
        splitPane.setOrientation(orientation);
    }

    /**
     * Checks if the pane is hidden
     * @param pane the pane to check
     * @return true if the pane is hidden, false otherwise
     */
    public boolean isPaneHidden(Node pane) {
        return !splitPane.getItems().contains(pane);
    }

    @Override
    protected void layoutChildren() {
        splitPane.resizeRelocate(0, 0, getWidth(), getHeight());
    }
}
