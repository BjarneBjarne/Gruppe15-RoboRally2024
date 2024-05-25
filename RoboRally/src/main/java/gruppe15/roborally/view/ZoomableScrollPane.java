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
 * - Using ZOOM_SPEED constant from the Constants.java class.
 * - Clamped the scaleValue between MIN_ZOOM and MAX_ZOOM, also from the Constants.java class.
 *
 * This file is used for an academic project at DTU - Danmarks Tekniske Universitet.
 *
 * License: This code is licensed under the Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0) license.
 *          You can view the full license at: https://creativecommons.org/licenses/by-sa/4.0/
 */

package gruppe15.roborally.view;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import static gruppe15.roborally.model.utils.Constants.*;

public class ZoomableScrollPane extends ScrollPane {
    private double scaleValue = 1;
    private Node target;
    private Node zoomNode;

    public ZoomableScrollPane(Node target) {
        super();
        this.target = target;
        this.zoomNode = new Group(target);
        setContent(outerNode(zoomNode));

        setPannable(true);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setFitToHeight(true); //center
        setFitToWidth(true); //center

        updateScale();
    }

    private Node outerNode(Node node) {
        Node outerNode = centeredNode(node);
        outerNode.setOnScroll(event -> {
            if (event.isControlDown()) {
                event.consume();
                onScroll(event.getTextDeltaY(), new Point2D(event.getX(), event.getY()));
            }
        });
        return outerNode;
    }

    private Node centeredNode(Node node) {
        VBox vBox = new VBox(node);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
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

        scaleValue = Math.clamp(scaleValue * zoomFactor, MIN_ZOOM, MAX_ZOOM) ;
        updateScale();
        this.layout(); // refresh ScrollPane scroll positions & target bounds

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
}
