package com.hagenberg.jarvis.views;

import com.hagenberg.jarvis.controllers.DebuggerController;
import com.hagenberg.jarvis.models.CallStackModel;
import com.hagenberg.jarvis.models.ObjectGraphModel;
import com.hagenberg.jarvis.models.entities.AccessModifier;
import com.hagenberg.jarvis.models.entities.CallStackFrame;
import com.hagenberg.jarvis.models.entities.graph.*;
import com.hagenberg.jarvis.util.SVGManager;
import com.hagenberg.jarvis.util.ServiceProvider;
import com.hagenberg.jarvis.views.components.CallStackCell;
import com.hagenberg.jarvis.views.components.HideableSplitPane;
import com.hagenberg.jarvis.views.components.AuxiliaryPane;
import com.hagenberg.jarvis.views.components.WindowMenu;
import com.hagenberg.jarvis.views.components.graph.*;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
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
        bindModelToGraph();
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

    private void bindModelToGraph() {

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

        TreeItem<GVariable> rootItem = new TreeItem<>(new GVariable("Root Node", null));
        rootItem.setExpanded(true);

        for (LocalGVariable localVariable : objectGraphModel.getNodes()) {
            rootItem.getChildren().add(buildTreeForNode(localVariable));
        }

        objectGraphModel.getNodes().addListener((ListChangeListener<LocalGVariable>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    System.out.println("Added " + change.getAddedSubList().size() + " elements");
                    for (LocalGVariable addedVar : change.getAddedSubList()) {
                        rootItem.getChildren().add(buildTreeForNode(addedVar));
                    }
                }
                if (change.wasRemoved()) {
                    for (LocalGVariable removedVar : change.getRemoved()) {
                        System.out.println("Removed " + removedVar.getName());
                        rootItem.getChildren().removeIf(treeItem -> treeItem.getValue().equals(removedVar));
                    }
                }
            }
        });

        TreeView<GVariable> treeView = new TreeView<>(rootItem);
        treeView.getStyleClass().add("object-graph-tree");
        treeView.setShowRoot(false);
        VBox.setVgrow(treeView, javafx.scene.layout.Priority.ALWAYS);

        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                TreeItem<GVariable> selectedItem = treeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    expandAllChildren(selectedItem);
                }
            }
        });

        treeView.setCellFactory(tv -> new TreeCell<>() {
            private final Label accessModifier = new Label();
            private final Label type = new Label();
            private final Label name = new Label();
            private final Label value = new Label();
            private final HBox container = new HBox(accessModifier, type, name, value);

            {
                container.getStyleClass().add("label-container");
                accessModifier.getStyleClass().add("access-modifier");
                type.getStyleClass().add("type");
                name.getStyleClass().add("var-name");
                value.getStyleClass().add("value");
            }

            @Override
            protected void updateItem(GVariable item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    GNode associatedNode = item.getNode();
                    type.setText(associatedNode.getType());
                    name.setText(item.getName());
                    if (item instanceof MemberGVariable memberGVariable) {
                        accessModifier.setText(memberGVariable.getAccessModifier().toString());
                    } else {
                        accessModifier.setText("");
                    }

                    if (associatedNode instanceof PrimitiveGNode primitiveGNode) {
                        value.setText(String.valueOf(primitiveGNode.getPrimitiveValue()));
                    } else if (associatedNode instanceof ReferenceGNode referenceGNode) {
                        value.setText("Reference to Object " + referenceGNode.getObject().getId());
                    } else if (associatedNode instanceof ArrayGNode arrayGNode) {
                        value.setText(arrayGNode.getContents().size() + " elements");
                    } else if (associatedNode instanceof ObjectGNode objectGNode) {
                        value.setText("id#" + objectGNode.getId());
                    }

                    setGraphic(container);
                }
            }
        });

        return treeView;
    }

    private void expandAllChildren(TreeItem<GVariable> treeItem) {
        if (treeItem != null && !treeItem.isLeaf()) {
            treeItem.setExpanded(true);
            for (TreeItem<GVariable> child : treeItem.getChildren()) {
                expandAllChildren(child);
            }
        }
    }

    private TreeItem<GVariable> buildTreeForNode(GVariable variable) {
        TreeItem<GVariable> treeItem = new TreeItem<>(variable);

        GNode associatedNode = variable.getNode();

        if (associatedNode instanceof ReferenceGNode referenceGNode) {
            ObjectGNode referencedObject = referenceGNode.getObject();
            GVariable wrappedVariable = GVariable.fromNode(referencedObject, "Object " + referencedObject.getId());
            treeItem.getChildren().add(buildTreeForNode(wrappedVariable));
        } else if (associatedNode instanceof ArrayGNode arrayGNode) {
            List<GNode> contents = arrayGNode.getContents();
            for (int i = 0; i < contents.size(); i++) {
                GNode elementNode = contents.get(i);
                GVariable wrappedVariable = GVariable.fromNode(elementNode, String.valueOf(i));
                treeItem.getChildren().add(buildTreeForNode(wrappedVariable));
            }
        } else if (associatedNode instanceof ObjectGNode objectGNode) {
            for (MemberGVariable memberGVariable : objectGNode.getMembers()) {
                treeItem.getChildren().add(buildTreeForNode(memberGVariable));
            }
        }
        return treeItem;
    }

    private ListView<CallStackFrame> buildCallStack() {
        ListView<CallStackFrame> callStackListView = new ListView<>();
        callStackListView.getStyleClass().add("call-stack");
        callStackListView.setCellFactory(frameListView -> new CallStackCell());
        callStackListView.setItems(callStackModel.getCallStack());
        VBox.setVgrow(callStackListView, javafx.scene.layout.Priority.ALWAYS);
        return callStackListView;
    }
}
