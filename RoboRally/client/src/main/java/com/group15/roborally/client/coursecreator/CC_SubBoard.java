package com.group15.roborally.client.coursecreator;

import com.group15.model.Heading;
import javafx.geometry.Point2D;
import javafx.scene.layout.GridPane;

public class CC_SubBoard {
    private final Point2D position;
    private final CC_SpaceView[][] spaceViews;
    private final GridPane gridPane;
    private final boolean isStartSubBoard;
    private final Heading direction;

    public CC_SubBoard(Point2D position, CC_SpaceView[][] spaceViews, GridPane gridPane, boolean isStartSubBoard, Heading direction) {
        this.position = position;
        this.spaceViews = spaceViews;
        this.gridPane = gridPane;
        this.isStartSubBoard = isStartSubBoard;
        this.direction = direction;
    }

    public Point2D getPosition() {
        return position;
    }

    public CC_SpaceView[][] getSpaceViews() {
        return spaceViews;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public boolean isStartSubBoard() {
        return isStartSubBoard;
    }

    public Heading getDirection() {
        return direction;
    }
}
