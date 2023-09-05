package com.hagenberg.jarvis.views.components.graph;

import com.hagenberg.jarvis.models.entities.AccessModifier;
import com.hagenberg.jarvis.models.entities.GraphObject;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class SimpleGraphNode implements GraphNode {

    private final Pane visual;

    public SimpleGraphNode(GraphObject object, double x, double y) {
        HBox header = new HBox();
        Label name = new Label(object.getName());
        name.getStyleClass().add("graph-node-name");
        Label type = new Label(object.getType());
        type.getStyleClass().add("simple-type");
        header.getStyleClass().add("graph-node-header");
        header.getChildren().addAll(name, type);

        VBox publicMembersContainer = buildMemberContainer("Public");
        VBox privateMembersContainer = buildMemberContainer("Private");

        for (GraphObject member : object.getMembers()) {
            HBox memberContainer = new HBox();

            if (member.isPrimitive()) {
                // Add name and value labels for each member
                memberContainer.getStyleClass().add("member-entry");
                Label memberName = new Label(member.getName());
                memberName.getStyleClass().add("var-name");
                Label memberType = new Label(" : " + member.getType());
                memberType.getStyleClass().add("simple-type");
                Label memberValue = new Label(" = " + member.getValue());
                memberValue.getStyleClass().add("value");

                memberContainer.getChildren().addAll(memberName, memberType, memberValue);
            } else {
                memberContainer.getChildren().add(new SimpleGraphNode(member, 0, 0).getNodeVisual());
            }

            if (AccessModifier.ENUM.isPublic(member.getAccessModifier())) {
                publicMembersContainer.getChildren().add(memberContainer);
            } else {
                privateMembersContainer.getChildren().add(memberContainer);
            }
        }

        VBox rootContainer = new VBox(10, header);
        if (publicMembersContainer.getChildren().size() > 1) {
            rootContainer.getChildren().add(publicMembersContainer);
        }
        if (privateMembersContainer.getChildren().size() > 1) {
            rootContainer.getChildren().add(privateMembersContainer);
        }
        rootContainer.getStyleClass().add("graph-node");

        visual = new Pane(rootContainer);
        visual.relocate(x, y);
    }

    private static VBox buildMemberContainer(String Public) {
        VBox publicMembersContainer = new VBox();
        publicMembersContainer.getStyleClass().add("members-container");
        publicMembersContainer.getChildren().add(new Label(Public));
        return publicMembersContainer;
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
