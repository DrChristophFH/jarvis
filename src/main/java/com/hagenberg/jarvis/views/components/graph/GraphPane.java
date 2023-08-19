package com.hagenberg.jarvis.views.components.graph;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;

public class GraphPane extends Region {

    private final Group contentGroup;
    private final Pane canvas;
    private final Affine affine;

    private double mouseX;
    private double mouseY;
    private Rectangle clippingRectangle;

    public GraphPane() {
        this.affine = new Affine();

        // content group
        this.contentGroup = new Group();
        contentGroup.getTransforms().add(affine);

        // canvas
        this.canvas = new Pane(contentGroup);
        canvas.getStyleClass().add("graph-canvas");
        getChildren().add(canvas);

        // clipping
        clippingRectangle = new Rectangle();
        canvas.setClip(clippingRectangle);

        setUpEventHandlers();
    }

    public void addGraphNode(GraphNode node) {
        Node visual = node.getNodeVisual();
        contentGroup.getChildren().add(visual);
    }

    private void setUpEventHandlers() {
        canvas.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });

        canvas.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.isMiddleButtonDown()) {
                double deltaX = (event.getX() - mouseX) / affine.getMxx();
                double deltaY = (event.getY() - mouseY) / affine.getMyy();

                affine.appendTranslation(deltaX, deltaY);

                mouseX = event.getX();
                mouseY = event.getY();

                event.consume();
            }
        });

        canvas.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                double delta = event.getDeltaY();
                double zoomFactor = 1.05;

                if (delta < 0) {
                    zoomFactor = 1 / zoomFactor;
                }

                Point2D mousePoint = new Point2D(event.getX(), event.getY());
                zoom(zoomFactor, mousePoint);

                event.consume();
            }
        });
    }

    private void zoom(double factor, Point2D zoomCenter) {
        affine.prependScale(factor, factor, zoomCenter.getX(), zoomCenter.getY());
    }

    @Override
    protected void layoutChildren() {
        canvas.resize(getWidth(), getHeight());
        clippingRectangle.setWidth(getWidth());
        clippingRectangle.setHeight(getHeight());
    }
}
