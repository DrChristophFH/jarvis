package com.hagenberg.jarvis.views;

import com.hagenberg.jarvis.views.components.AuxiliaryPane;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
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
        rootSplitPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        rootPane.setCenter(rootSplitPane);

        AuxiliaryPane classes = new AuxiliaryPane("Classes");
        SplitPane leftAuxiliaryContainer = new SplitPane(classes);
        rootSplitPane.getItems().add(leftAuxiliaryContainer);
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
