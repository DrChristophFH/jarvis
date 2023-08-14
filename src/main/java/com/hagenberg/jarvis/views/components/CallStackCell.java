package com.hagenberg.jarvis.views.components;

import com.hagenberg.jarvis.models.entities.CallStackFrame;
import com.hagenberg.jarvis.models.entities.MethodParameter;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class CallStackCell extends ListCell<CallStackFrame> {
    private final Label methodName = new Label();
    {
        methodName.getStyleClass().add("method-name");
    }
    private final Label lineNumber = new Label();
    {
        lineNumber.getStyleClass().add("line-number");
    }
    private final Label className = new Label();
    {
        className.getStyleClass().add("class-name");
    }
    private final CustomizableTitledPane container = new CustomizableTitledPane();
    {
        container.getStyleClass().add("call-stack-cell-container");
        container.header().getChildren().addAll(methodName, lineNumber, className);
    }

    /**
     * Updates the cell with the given CallStackFrame.
     * @implNote Avoids the creation of new objects by reusing the same objects.
     * @param frame the CallStackFrame to be displayed
     * @param empty whether the cell is empty or not
     */
    @Override
    protected void updateItem(CallStackFrame frame, boolean empty) {
        super.updateItem(frame, empty);

        if (empty || frame == null) {
            setText(null);
            setGraphic(null);
        } else {
            methodName.setText(buildMethodSignature(frame));
            className.setText(frame.getClassName());
            lineNumber.setText(": " + frame.getLineNumber());
            container.setContent(buildPropertiesPane(frame));
            setGraphic(container);
        }
    }

    private String buildMethodSignature(CallStackFrame frame) {
        StringBuffer sb = new StringBuffer();
        sb.append(frame.getMethodName()).append("(");
        for (MethodParameter parameter : frame.getParameters()) {
            sb.append(parameter.getSimpleType()).append(" ").append(parameter.getName()).append(", ");
        }
        if (!frame.getParameters().isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");
        return sb.toString();
    }

    private Node buildPropertiesPane(CallStackFrame frame) {
        VBox propertiesList = new VBox();
        propertiesList.getStyleClass().add("properties-list");

        for (MethodParameter parameter : frame.getParameters()) {
            HBox property = new HBox();
            property.getStyleClass().add("property-cell");

            Label simpleType = new Label(parameter.getSimpleType());
            simpleType.getStyleClass().add("simple-type");

            Label name = new Label(parameter.getName());
            name.getStyleClass().add("var-name");

            Label value = new Label(parameter.getValue());
            value.getStyleClass().add("value");

            property.getChildren().addAll(simpleType, name, new Label(": "), value);
            propertiesList.getChildren().add(property);
        }

        return propertiesList;
    }
}
