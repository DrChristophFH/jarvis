package com.hagenberg.jarvis.views.components;

import com.hagenberg.jarvis.models.entities.CallStackFrame;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class CallStackCell extends ListCell<CallStackFrame> {

    @Override
    protected void updateItem(CallStackFrame frame, boolean empty) {
        super.updateItem(frame, empty);

        if (empty || frame == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Displaying class name and method name
            Label label = new Label(frame.getClassName() + "." + frame.getMethodName());

            // This can be a button or some GUI element to trigger the expansion
            Button expandButton = new Button("Expand");
            expandButton.setOnAction(e -> {
                // Handle the expansion. For simplicity, this will show parameters in a new window.
                // You can have a more sophisticated handling.
                showParameters(frame.getParameters());
            });

            HBox container = new HBox(label, expandButton);
            setGraphic(container);
        }
    }

    private void showParameters(List<String> parameters) {
        // This will open up a new dialog to show parameters.
        // This can be replaced by any custom behavior like inline expansion, etc.
        ListView<String> parametersView = new ListView<>(FXCollections.observableArrayList(parameters));
        Stage stage = new Stage();
        stage.setScene(new Scene(parametersView));
        stage.show();
    }
}
