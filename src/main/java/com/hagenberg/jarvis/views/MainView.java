package com.hagenberg.jarvis.views;

import com.hagenberg.jarvis.views.components.HideableSplitPane;
import com.hagenberg.jarvis.views.components.AuxiliaryPane;
import com.hagenberg.jarvis.views.components.WindowMenu;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

        WindowMenu leftWindowMenu = new WindowMenu();
        rootPane.setLeft(leftWindowMenu);

        WindowMenu rightWindowMenu = new WindowMenu(true);
        rootPane.setRight(rightWindowMenu);

        HBox bottomStatusMenu = new HBox();
        bottomStatusMenu.getStyleClass().add("status-bar");
        rootPane.setBottom(bottomStatusMenu);

        SplitPane rootSplitPane = new SplitPane();
        rootSplitPane.getStyleClass().add("root-split-pane");
        rootSplitPane.setOrientation(Orientation.VERTICAL);
        rootPane.setCenter(rootSplitPane);

        HideableSplitPane mainSplitPane = new HideableSplitPane();
        mainSplitPane.getStyleClass().add("main-split-pane");
        mainSplitPane.setOrientation(Orientation.HORIZONTAL);
        rootSplitPane.getItems().add(mainSplitPane);

        SplitPane supportSplitPane = new SplitPane();
        supportSplitPane.getStyleClass().add("support-split-pane");
        supportSplitPane.setOrientation(Orientation.HORIZONTAL);
        rootSplitPane.getItems().add(supportSplitPane);

        HideableSplitPane leftAuxiliaryContainer = new HideableSplitPane();
        AuxiliaryPane classes = new AuxiliaryPane("Classes");
        AuxiliaryPane classInfo = new AuxiliaryPane("Class Info");
        leftAuxiliaryContainer.addPanes(classes, classInfo);
        leftAuxiliaryContainer.setOrientation(Orientation.VERTICAL);
        mainSplitPane.addPane(leftAuxiliaryContainer);
        leftWindowMenu.addWindow(classes, "/icons/classes.svg");
        leftWindowMenu.addWindow(classInfo, "/icons/class-info.svg");

        ScrollPane objectGraph = new ScrollPane();
        objectGraph.getStyleClass().add("object-graph");
        mainSplitPane.addPane(objectGraph);

        HideableSplitPane rightAuxiliaryContainer = new HideableSplitPane();
        AuxiliaryPane visControl = new AuxiliaryPane("Visualization Controls");
        AuxiliaryPane callStack = new AuxiliaryPane("Call Stack");
        rightAuxiliaryContainer.addPanes(visControl, callStack);
        rightAuxiliaryContainer.setOrientation(Orientation.VERTICAL);
        mainSplitPane.addPane(rightAuxiliaryContainer);
        rightWindowMenu.addWindow(visControl, "/icons/visualization-controls.svg");
        rightWindowMenu.addWindow(callStack, "/icons/call-stack.svg");
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
