package com.hagenberg.jarvis.views;

import com.hagenberg.jarvis.controllers.DebuggerController;
import com.hagenberg.jarvis.models.CallStackModel;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.CallStackFrame;
import com.hagenberg.jarvis.models.entities.GraphObject;
import com.hagenberg.jarvis.util.SVGManager;
import com.hagenberg.jarvis.util.ServiceProvider;
import com.hagenberg.jarvis.views.components.CallStackCell;
import com.hagenberg.jarvis.views.components.HideableSplitPane;
import com.hagenberg.jarvis.views.components.AuxiliaryPane;
import com.hagenberg.jarvis.views.components.WindowMenu;
import com.hagenberg.jarvis.views.components.graph.*;
import javafx.beans.InvalidationListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class MainView {
    private final CallStackModel callStackModel = ServiceProvider.getInstance().getDependency(CallStackModel.class);
    private final DebuggerController debuggerController = ServiceProvider.getInstance().getDependency(DebuggerController.class);

    private Scene scene;
    private GraphPane graph = new GraphPane();

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
        rootSplitPane.setDividerPositions(0.8, 0.2);
        rootPane.setCenter(rootSplitPane);

        HideableSplitPane mainSplitPane = new HideableSplitPane();
        mainSplitPane.getStyleClass().add("main-split-pane");
        mainSplitPane.setOrientation(Orientation.HORIZONTAL);
        mainSplitPane.setDividerPositions(0.2, 0.8);
        rootSplitPane.getItems().add(mainSplitPane);

        SplitPane supportSplitPane = new SplitPane();
        supportSplitPane.getStyleClass().add("support-split-pane");
        supportSplitPane.setOrientation(Orientation.HORIZONTAL);
        rootSplitPane.getItems().add(supportSplitPane);

        HideableSplitPane leftAuxiliaryContainer = new HideableSplitPane();
        AuxiliaryPane classes = new AuxiliaryPane("Classes");
        AuxiliaryPane classInfo = new AuxiliaryPane("Class Info");
        classes.setContent(buildTreeViewOfObjectGraph());
        leftAuxiliaryContainer.addPanes(classes, classInfo);
        leftAuxiliaryContainer.setOrientation(Orientation.VERTICAL);
        mainSplitPane.addPane(leftAuxiliaryContainer);
        leftWindowMenu.addWindow(classes, "/icons/classes.svg");
        leftWindowMenu.addWindow(classInfo, "/icons/class-info.svg");

        VBox objectGraphContainer = new VBox();
        objectGraphContainer.getStyleClass().add("object-graph-container");
        graph = new GraphPane();
        addGraphComponents();
        VBox.setVgrow(graph, javafx.scene.layout.Priority.ALWAYS);
        HBox debugMenu = buildDebugMenu();
        objectGraphContainer.getChildren().addAll(graph, debugMenu);
        mainSplitPane.addPane(objectGraphContainer);

        HideableSplitPane rightAuxiliaryContainer = new HideableSplitPane();
        AuxiliaryPane visControl = new AuxiliaryPane("Visualization Controls");
        AuxiliaryPane callStack = new AuxiliaryPane("Call Stack");
        callStack.setContent(buildCallStack());
        rightAuxiliaryContainer.addPanes(visControl, callStack);
        rightAuxiliaryContainer.setOrientation(Orientation.VERTICAL);
        mainSplitPane.addPane(rightAuxiliaryContainer);
        rightWindowMenu.addWindow(visControl, "/icons/visualization-controls.svg");
        rightWindowMenu.addWindow(callStack, "/icons/call-stack.svg");
    }

    public void show(Stage stage) {
        stage.setScene(scene);
        stage.setTitle("JARVIS");
        stage.getIcons().add(new Image("/icons/logo.png"));
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setResizable(true);
        stage.show();
    }

    private void addGraphComponents() {
        SimpleGraphNode node1 = new SimpleGraphNode("A", 50, 50);
        SimpleGraphNode node2 = new SimpleGraphNode("B", 200, 100);
        SimpleGraphNode node3 = new SimpleGraphNode("C", 350, 50);

        graph.addGraphNode(node1);
        graph.addGraphNode(node2);
        graph.addGraphNode(node3);
    }

    private HBox buildDebugMenu() {
        HBox debugMenu = new HBox();
        debugMenu.getStyleClass().add("debug-menu");

        Button stepInto = buildIconButton("Step Into", "/icons/step-into.svg");
        Button stepOut = buildIconButton("Step Out", "/icons/step-out.svg");
        Button stepOver = buildIconButton("Step Over", "/icons/step-over.svg");
        Button resume = buildIconButton("Resume", "/icons/resume.svg");

        stepInto.setOnAction(event -> debuggerController.onStepIntoButtonClicked());
        stepOut.setOnAction(event -> debuggerController.onStepOutButtonClicked());
        stepOver.setOnAction(event -> debuggerController.onStepOverButtonClicked());
        resume.setOnAction(event -> debuggerController.onResumeButtonClicked());

        debugMenu.getChildren().addAll(stepInto, stepOut, stepOver, resume);

        return debugMenu;
    }

    private Button buildIconButton(String text, String resourceName) {
        Button button = new Button();
        button.getStyleClass().add("text-icon-button");
        button.setText(text);
        button.setGraphic(SVGManager.getInstance().getSVG(resourceName));
        return button;
    }

    private Node buildTreeViewOfObjectGraph() {
        ObjectGraphModel objectGraphModel = ServiceProvider.getInstance().getDependency(ObjectGraphModel.class);
        TreeItem<GraphObject> root = new TreeItem<>();
        TreeView<GraphObject> treeView = new TreeView<>(root);
        treeView.setShowRoot(false);
        treeView.getStyleClass().add("object-graph-tree");

        // recursively build the tree from the object graph model
        for (GraphObject rootObject : objectGraphModel.getRootObjects()) {
            root.getChildren().add(buildTreeItem(rootObject));
            System.out.println("Adding root object " + rootObject.getName());
        }

        // add change listener to object graph model to update the tree view
        objectGraphModel.getRootObjects().addListener((InvalidationListener) Observable -> {
            root.getChildren().clear();
            System.out.println("Root objects changed");
            for (GraphObject rootObject : objectGraphModel.getRootObjects()) {
                System.out.println("Adding root object " + rootObject.getName());
                root.getChildren().add(buildTreeItem(rootObject));
            }
        });

        // set the cell factory to display the graph objects
        treeView.setCellFactory(p -> new TreeCell<>() {
            private final Label type = new Label();
            private final Label name = new Label();
            private final Label value = new Label();
            private final HBox container = new HBox(type, name, value);

            {
                container.getStyleClass().add("label-container");
                type.getStyleClass().add("type");
                name.getStyleClass().add("var-name");
                value.getStyleClass().add("value");
            }

            @Override
            protected void updateItem(GraphObject item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    type.setText(item.getType());
                    name.setText(item.getName());
                    value.setText(item.getValue());
                    setGraphic(container);
                }
            }
        });

        return treeView;
    }

    private TreeItem<GraphObject> buildTreeItem(GraphObject rootObject) {
        TreeItem<GraphObject> treeItem = new TreeItem<>(rootObject);
        treeItem.setExpanded(true);
        for (GraphObject members : rootObject.getMembers()) {
            treeItem.getChildren().add(buildTreeItem(members));
        }
        return treeItem;
    }

    private ListView<CallStackFrame> buildCallStack() {
        ListView<CallStackFrame> callStackListView = new ListView<>();
        callStackListView.getStyleClass().add("call-stack");
        callStackListView.setCellFactory(frameListView -> new CallStackCell());
        callStackListView.setItems(callStackModel.getCallStack());
        return callStackListView;
    }
}
