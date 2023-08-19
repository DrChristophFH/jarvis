package com.hagenberg.jarvis.views.components.graph;

import com.hagenberg.jarvis.models.entities.GraphObject;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class SimpleGraphNode implements GraphNode {

    private final Pane visual;

    public SimpleGraphNode(GraphObject object, double x, double y) {
        Label name = new Label(object.getName());

        VBox publicMembersContainer = new VBox(5); // spacing between members
        publicMembersContainer.getStyleClass().add("members-container");
        publicMembersContainer.getChildren().add(new Label("Public"));

        VBox privateMembersContainer = new VBox(5); // spacing between members
        privateMembersContainer.getStyleClass().add("members-container");
        privateMembersContainer.getChildren().add(new Label("Private"));

        for (GraphObject member : object.getMembers()) {
            // Add name and value labels for each member
            HBox memberContainer = new HBox();
            memberContainer.getStyleClass().add("member-entry");
            Label memberName = new Label(member.getName());
            memberName.getStyleClass().add("var-name");
            Label memberType = new Label(" : " + member.getType());
            memberType.getStyleClass().add("simple-type");
            Label memberValue = new Label(" = " + member.getValue());
            memberValue.getStyleClass().add("value");

            memberContainer.getChildren().addAll(memberName, memberType, memberValue);

            publicMembersContainer.getChildren().add(memberContainer);
            privateMembersContainer.getChildren().add(memberContainer);
        }

        VBox rootContainer = new VBox(10, name, publicMembersContainer, privateMembersContainer); // spacing between main elements
        rootContainer.getStyleClass().add("graph-node");

        visual = new Pane(rootContainer);
        visual.relocate(x, y);
    }

    @Override
    public Pane getNodeVisual() {
        return visual;
    }

    @Override
    public void setPosition(double x, double y) {
        visual.relocate(x, y);
    }
}
