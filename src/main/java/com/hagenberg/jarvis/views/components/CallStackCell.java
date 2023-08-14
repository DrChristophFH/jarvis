package com.hagenberg.jarvis.views.components;

import com.hagenberg.jarvis.models.entities.CallStackFrame;
import com.hagenberg.jarvis.models.entities.MethodParameter;
import com.hagenberg.jarvis.util.SVGManager;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.util.List;

public class CallStackCell extends ListCell<CallStackFrame> {
    private final Button expandButton = new Button();
    {
        expandButton.getStyleClass().add("icon-button");
        expandButton.setGraphic(SVGManager.getInstance().getSVG("/icons/method-inspect.svg"));
    }
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
    private final HBox container = new HBox();
    {
        container.getStyleClass().add("call-stack-cell-container");
        container.getChildren().addAll(expandButton, methodName, lineNumber, className);
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
            expandButton.setOnAction(e -> showParameters(frame.getParameters()));
            lineNumber.setText(": " + frame.getLineNumber());
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

    private void showParameters(List<MethodParameter> parameters) {

    }
}
