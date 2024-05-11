/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package gruppe15.roborally.view;

import gruppe15.observer.Subject;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.boardelements.BE_EnergySpace;
import gruppe15.roborally.model.boardelements.BE_Reboot;
import gruppe15.roborally.model.boardelements.BoardElement;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    public final Space space;
    public final static int SPACE_HEIGHT = 50; // 60; // 75;
    public final static int SPACE_WIDTH = 50;  // 60; // 75;
    private final ImageView backgroundImageView = new ImageView();
    private final ImageView boardElementImageView = new ImageView();
    private final ImageView energyCubeImageView = new ImageView();
    private final List<ImageView> laserImageViews = new ArrayList<>();
    private final List<ImageView> wallImageViews = new ArrayList<>();
    private GridPane directionOptionsPane = null;

    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        backgroundImageView.setFitWidth(SPACE_WIDTH);
        backgroundImageView.setFitHeight(SPACE_HEIGHT);
        backgroundImageView.setImage(space.getImage());
        this.getChildren().add(backgroundImageView);

        BoardElement boardElement = space.getBoardElement();
        if (boardElement != null) {
            if (boardElement instanceof BE_Reboot) {
                boardElementImageView.setFitWidth(SPACE_WIDTH * 0.75);
                boardElementImageView.setFitHeight(SPACE_HEIGHT * 0.75);
            } else {
                boardElementImageView.setFitWidth(SPACE_WIDTH);
                boardElementImageView.setFitHeight(SPACE_HEIGHT);
            }

            boardElementImageView.setImage(boardElement.getImage());
            this.getChildren().add(boardElementImageView);

            if (boardElement instanceof BE_EnergySpace) {
                energyCubeImageView.setFitWidth(SPACE_WIDTH);
                energyCubeImageView.setFitHeight(SPACE_HEIGHT);
                energyCubeImageView.setImage(ImageUtils.getImageFromName("energyCube.png"));
                energyCubeImageView.xProperty();
                energyCubeImageView.setLayoutY(10);
            }
        }

        Image wallImage = ImageUtils.getImageFromName("wall.png");
        for (Heading wall : space.getWalls()) {
            ImageView wallImageView = new ImageView();
            wallImageView.setFitWidth(SPACE_WIDTH);
            wallImageView.setFitHeight(SPACE_HEIGHT);
            wallImageView.setImage(ImageUtils.getRotatedImageByHeading(wallImage, wall));
            wallImageViews.add(wallImageView);
            this.getChildren().add(wallImageView);
        }

        /*if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }*/

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updateSpace() {
        this.getChildren().clear();
        this.getChildren().add(backgroundImageView);

        BoardElement boardElement = space.getBoardElement();
        if (boardElement != null) {
            this.getChildren().add(boardElementImageView);

            if (boardElement instanceof BE_EnergySpace energySpace) {
                if (energySpace.getHasEnergyCube()) {
                    this.getChildren().add(energyCubeImageView);
                }
            }
        }

        this.laserImageViews.clear();
        for (Heading laser : space.getLasersOnSpace()) {
            ImageView laserImageView = newLaserImageView(laser);
            this.laserImageViews.add(laserImageView);
            this.getChildren().add(laserImageView);
        }

        for (ImageView wall : wallImageViews) {
            this.getChildren().add(wall);
        }

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    private ImageView newLaserImageView(Heading laser) {
        ImageView laserImageView = new ImageView();
        laserImageView.setFitWidth(SPACE_WIDTH);
        laserImageView.setFitHeight(SPACE_HEIGHT);
        laserImageView.setImage(ImageUtils.getRotatedImageByHeading(ImageUtils.getImageFromName("laser.png"), laser));
        return laserImageView;
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updateSpace();
        }
    }

    public void setDirectionOptionsPane(GridPane directionOptionsPane) {
        this.directionOptionsPane = directionOptionsPane;
        System.out.println("Set space: " + space.x + ", " + space.y + " to choose.");
    }

    public void removeDirectionOptionsPane() {
        this.directionOptionsPane = null;
    }
}
