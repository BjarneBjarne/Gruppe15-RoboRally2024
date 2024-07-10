/*
 * This class is an edited version of a class originally posted on StackOverflow.
 *
 * Original Author: Daniel Hári - https://stackoverflow.com/users/1386911/daniel-h%c3%a1ri
 * Original Post: https://stackoverflow.com/a/44314455
 *
 * Modifications made by: Carl Gustav Bjergaard Aggeboe - s235063
 *
 * Description of Modifications:
 * - Set scroll event to only zoom when the CTRL button is pressed.
 * - Using ZOOM_SPEED constant from the ApplicationSettings.java class.
 * - Clamped the scaleValue between MIN_ZOOM and MAX_ZOOM, also from the ApplicationSettings.java class.
 *
 * This file is used for an academic project at DTU - Danmarks Tekniske Universitet.
 *
 * License: This code is licensed under the Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0) license.
 *          You can view the full license at: https://creativecommons.org/licenses/by-sa/4.0/
 */

package com.group15.roborally.client.view;

import com.group15.roborally.client.RoboRally;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.VBox;

import static com.group15.roborally.client.ApplicationSettings.*;

public class ZoomableScrollPane extends ScrollPane {
    private static final double BASE_MOUSE_ZOOM_SPEED = 0.16;
    private static final double BASE_TOUCHPAD_ZOOM_SPEED = 60;

    private double scaleValue;
    private final Node target;
    private final Group zoomNode;
    private double frameTimeDelta;

    public ZoomableScrollPane(Node target) {
        this(target, 1);
    }

    public ZoomableScrollPane(Node target, double startScale) {
        super();
        this.target = target;
        this.zoomNode = new Group(target);
        this.scaleValue = startScale;

        VBox outerNode = new VBox(zoomNode);
        outerNode.setAlignment(Pos.CENTER);
        configureZoomHandlers(outerNode);

        this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.R) {
                resetScale(startScale);
            }
        });

        setContent(outerNode);
        configureScrollPaneProperties();

        updateScale();
        this.layoutBoundsProperty().addListener((_, _, _) -> Platform.runLater(this::centerContent));

        // Animation timer to calculate the frame time delta
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate != 0) {
                    frameTimeDelta = (now - lastUpdate) / 1e9;
                }
                lastUpdate = now;
            }
        };
        timer.start();
    }

    private void configureZoomHandlers(VBox outerNode) {
        outerNode.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                event.consume();
                double wheelDelta = event.getDeltaY() * BASE_MOUSE_ZOOM_SPEED * ZOOM_SPEED * 0.025;
                Point2D mousePoint = new Point2D(event.getX(), event.getY());
                //RoboRally.addDebugText("Scrolling with: " + String.format("%.3f", wheelDelta) + ". Else: " + String.format("%.3f", event.getDeltaY() * BASE_MOUSE_ZOOM_SPEED * ZOOM_SPEED * frameTimeDelta), 1);
                onScroll(wheelDelta, mousePoint);
            }
        });

        outerNode.addEventFilter(ZoomEvent.ZOOM, event -> {
            double zoomFactor = event.getZoomFactor();
            double wheelDelta = Math.log(zoomFactor) * BASE_TOUCHPAD_ZOOM_SPEED * ZOOM_SPEED * frameTimeDelta;
            Point2D mousePoint = new Point2D(event.getX(), event.getY());
            onScroll(wheelDelta, mousePoint);
            event.consume();
        });
    }

    private void configureScrollPaneProperties() {
        setPannable(false);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setFitToHeight(true);
        setFitToWidth(true);
    }

    private void updateScale() {
        target.setScaleX(scaleValue);
        target.setScaleY(scaleValue);
    }

    private void resetScale(double startScale) {
        scaleValue = startScale;
        updateScale();
        this.layout();
        centerContent();
    }

    private void onScroll(double wheelDelta, Point2D mousePoint) {
        double zoomFactor = Math.exp(wheelDelta);

        Bounds innerBounds = zoomNode.getLayoutBounds();
        Bounds viewportBounds = getViewportBounds();

        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        if (isZoomLimitReached(zoomFactor, wheelDelta)) return;

        scaleValue *= zoomFactor;
        updateScale();
        this.layout();

        Point2D posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(mousePoint));
        Point2D adjustment = target.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        adjustScrollPosition(valX, valY, adjustment, updatedInnerBounds, viewportBounds);
    }

    private boolean isZoomLimitReached(double zoomFactor, double wheelDelta) {
        return (scaleValue * zoomFactor <= MIN_ZOOM + 0.01 && wheelDelta < 0) ||
                (scaleValue * zoomFactor >= MAX_ZOOM - 0.01 && wheelDelta > 0);
    }

    private void adjustScrollPosition(double valX, double valY, Point2D adjustment, Bounds updatedInnerBounds, Bounds viewportBounds) {
        double newHValue = (valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth());
        double newVValue = (valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight());

        this.setHvalue(clamp(newHValue));
        this.setVvalue(clamp(newVValue));
    }

    private double clamp(double value) {
        return Math.min(Math.max(value, 0), 1);
    }

    private void centerContent() {
        this.setHvalue(0.5);
        this.setVvalue(0.5);
    }
}