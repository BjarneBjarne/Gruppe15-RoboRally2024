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
 * - Using ZOOM_SPEED constant from the GameVariables.java class.
 * - Clamped the scaleValue between MIN_ZOOM and MAX_ZOOM, also from the GameVariables.java class.
 *
 * This file is used for an academic project at DTU - Danmarks Tekniske Universitet.
 *
 * License: This code is licensed under the Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0) license.
 *          You can view the full license at: https://creativecommons.org/licenses/by-sa/4.0/
 */

package gruppe15.roborally.view;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import static gruppe15.roborally.GameVariables.*;

public class ZoomableScrollPane extends ScrollPane {
    private double scaleValue = 1;
    private final StackPane target;
    private final Group zoomNode;

    public ZoomableScrollPane(StackPane target) {
        super();
        this.target = target;
        this.zoomNode = new Group(target);

        VBox outerNode = new VBox(zoomNode);
        outerNode.setAlignment(Pos.CENTER);
        outerNode.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                onScroll(event.getTextDeltaY(), new Point2D(event.getX(), event.getY()));
                event.consume();
            }
        });
        this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.R) {
                scaleValue = 1;
                updateScale();
                this.layout();
                centerContent();
            }
        });

        setContent(outerNode);

        setPannable(false);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setFitToHeight(true); //center
        setFitToWidth(true); //center

        updateScale();

        this.layoutBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            Platform.runLater(this::centerContent);
        });
    }

    private void updateScale() {
        target.setScaleX(scaleValue);
        target.setScaleY(scaleValue);
    }

    private void onScroll(double wheelDelta, Point2D mousePoint) {
        double zoomFactor = Math.exp(wheelDelta * ZOOM_SPEED);

        Bounds innerBounds = zoomNode.getLayoutBounds();
        Bounds viewportBounds = getViewportBounds();

        // calculate pixel offsets from [0, 1] range
        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        if ((scaleValue * zoomFactor) - MIN_ZOOM <= 0.01 && wheelDelta < 0) {
            return;
        }
        if ((scaleValue * zoomFactor) - MAX_ZOOM >= 0.01 && wheelDelta > 0) {
            return;
        }
        scaleValue = scaleValue * zoomFactor;

        updateScale();
        this.layout();

        // convert target coordinates to zoomTarget coordinates
        Point2D posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(mousePoint));

        // calculate adjustment of scroll position (pixels)
        Point2D adjustment = target.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        // convert back to [0, 1] range
        // (too large/small values are automatically corrected by ScrollPane)
        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        this.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
        this.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
    }

    private void centerContent() {
        // Set the scroll position to the center
        this.setHvalue(0.5);
        this.setVvalue(0.5);
    }
}
