package com.hagenberg.jarvis.views;

import com.hagenberg.jarvis.views.components.AuxiliaryPane;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class MainView {
    private Scene scene;

    public MainView() {
        buildScene();
    }

    /**
     * Builds the scene graph top-down
     */
    private void buildScene() {
        BorderPane rootPane = new BorderPane();
        scene = new Scene(rootPane, 1600, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/jarvis.css")).toExternalForm());

        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().add("menu-bar");
        rootPane.setTop(menuBar);

        VBox leftWindowMenu = new VBox();
        leftWindowMenu.getStyleClass().add("window-menu");
        rootPane.setLeft(leftWindowMenu);

        VBox rightWindowMenu = new VBox();
        rightWindowMenu.getStyleClass().add("window-menu");
        rootPane.setRight(rightWindowMenu);

        HBox bottomStatusMenu = new HBox();
        bottomStatusMenu.getStyleClass().add("status-bar");
        rootPane.setBottom(bottomStatusMenu);

        SplitPane rootSplitPane = new SplitPane();
        rootSplitPane.getStyleClass().add("root-split-pane");
        rootSplitPane.setOrientation(Orientation.VERTICAL);
        rootPane.setCenter(rootSplitPane);

        SplitPane mainSplitPane = new SplitPane();
        mainSplitPane.getStyleClass().add("main-split-pane");
        mainSplitPane.setOrientation(Orientation.HORIZONTAL);
        rootSplitPane.getItems().add(mainSplitPane);

        SplitPane supportSplitPane = new SplitPane();
        supportSplitPane.getStyleClass().add("support-split-pane");
        supportSplitPane.setOrientation(Orientation.HORIZONTAL);
        rootSplitPane.getItems().add(supportSplitPane);

        AuxiliaryPane classes = new AuxiliaryPane("Classes");
        AuxiliaryPane stuff = new AuxiliaryPane("Stuff");
        SplitPane leftAuxiliaryContainer = new SplitPane(classes, stuff);
        leftAuxiliaryContainer.setOrientation(Orientation.VERTICAL);
        mainSplitPane.getItems().add(leftAuxiliaryContainer);

        ScrollPane objectGraph = new ScrollPane();
        objectGraph.getStyleClass().add("object-graph");
        mainSplitPane.getItems().add(objectGraph);

        AuxiliaryPane visControl = new AuxiliaryPane("Visualization Controls");
        AuxiliaryPane vis = new AuxiliaryPane("Visualization");
        SplitPane rightAuxiliaryContainer = new SplitPane(visControl, vis);
        rightAuxiliaryContainer.setOrientation(Orientation.VERTICAL);
        mainSplitPane.getItems().add(rightAuxiliaryContainer);
    }

    public void show(Stage stage) {
        stage.setScene(scene);
        stage.setTitle("JARVIS");
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setResizable(true);
        stage.show();
    }

    public void hide() {
    }
}
