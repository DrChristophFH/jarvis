package com.hagenberg.jarvis.views;

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
        rootPane.setTop(menuBar);

        VBox leftWindowMenu = new VBox();
        rootPane.setLeft(leftWindowMenu);

        VBox rightWindowMenu = new VBox();
        rootPane.setRight(rightWindowMenu);

        HBox bottomStatusMenu = new HBox();
        rootPane.setBottom(bottomStatusMenu);

        SplitPane rootSplitPane = new SplitPane();
        rootPane.setCenter(rootSplitPane);
        rootSplitPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
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
