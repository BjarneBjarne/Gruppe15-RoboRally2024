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
package com.group15.roborally.client.view;

import com.group15.roborally.common.observer.Subject;
import com.group15.roborally.common.observer.ViewObserver;
import com.group15.roborally.client.ApplicationSettings;
import com.group15.roborally.client.model.Heading;
import com.group15.roborally.client.model.Laser;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.Space;
import com.group15.roborally.client.model.boardelements.BE_ConveyorBelt;
import com.group15.roborally.client.model.boardelements.BE_EnergySpace;
import com.group15.roborally.client.model.boardelements.BoardElement;
import com.group15.roborally.client.utils.ImageUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.group15.roborally.client.ApplicationSettings.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {
    public final Space space;
    private final ImageView backgroundImageView = new ImageView();
    private final ImageView boardElementImageView = new ImageView();
    private final ImageView energyCubeImageView = new ImageView();
    private final List<ImageView> wallImageViews = new ArrayList<>();
    private final ImageView playerImageView = new ImageView();
    private final ImageView readyCheckImageView = new ImageView();
    private final ImageView checkpointImageView = new ImageView();
    private boolean usingPlayerRebootImage = false;
    private Image playerRebootImage;
    private final Text spaceCoords = new Text();
    private final Text debugText = new Text();

    private final ImageView interactionImageView = new ImageView();
    private Timeline rotationTimeline;

    public SpaceView(@NotNull Space space) {
        this.space = space;

        this.setPrefWidth(ApplicationSettings.SPACE_SIZE);
        this.setMinWidth(ApplicationSettings.SPACE_SIZE);
        this.setMaxWidth(ApplicationSettings.SPACE_SIZE);

        this.setPrefHeight(SPACE_SIZE);
        this.setMinHeight(SPACE_SIZE);
        this.setMaxHeight(SPACE_SIZE);

        backgroundImageView.setFitWidth(ApplicationSettings.SPACE_SIZE);
        backgroundImageView.setFitHeight(SPACE_SIZE);
        backgroundImageView.setImage(space.getImage());
        this.getChildren().add(backgroundImageView);

        checkpointImageView.setFitWidth(ApplicationSettings.SPACE_SIZE);
        checkpointImageView.setFitHeight(SPACE_SIZE);
        this.getChildren().add(checkpointImageView);

        BoardElement boardElement = space.getBoardElement();
        if (boardElement != null) {
            boardElementImageView.setFitWidth(ApplicationSettings.SPACE_SIZE);
            boardElementImageView.setFitHeight(SPACE_SIZE);

            boardElementImageView.setImage(boardElement.getImage());
            this.getChildren().add(boardElementImageView);

            if (boardElement instanceof BE_EnergySpace) {
                energyCubeImageView.setFitWidth(ApplicationSettings.SPACE_SIZE);
                energyCubeImageView.setFitHeight(SPACE_SIZE);
                energyCubeImageView.setImage(ImageUtils.getImageFromName("Board_Pieces/energyCube.png"));
                energyCubeImageView.setLayoutY(10);
            }
        }

        Image wallImage = ImageUtils.getImageFromName("Board_Pieces/wall.png");
        for (Heading wall : space.getWalls()) {
            ImageView wallImageView = new ImageView();
            wallImageView.setFitWidth(ApplicationSettings.SPACE_SIZE);
            wallImageView.setFitHeight(SPACE_SIZE);
            wallImageView.setImage(ImageUtils.getRotatedImageByHeading(wallImage, wall));
            wallImageViews.add(wallImageView);
            this.getChildren().add(wallImageView);
        }

        playerImageView.setFitWidth(ApplicationSettings.SPACE_SIZE);
        playerImageView.setFitHeight(SPACE_SIZE);
        this.getChildren().add(playerImageView);

        readyCheckImageView.setFitWidth(ApplicationSettings.SPACE_SIZE / 2);
        readyCheckImageView.setFitHeight(SPACE_SIZE / 2);
        readyCheckImageView.setImage(ImageUtils.getImageFromName("ReadyCheck.png"));
        readyCheckImageView.setVisible(false);
        this.getChildren().add(readyCheckImageView);

        interactionImageView.setFitWidth(ApplicationSettings.SPACE_SIZE / 2);
        interactionImageView.setFitHeight(SPACE_SIZE / 2);
        interactionImageView.setImage(ImageUtils.getImageFromName("InteractionLoading.png"));
        interactionImageView.setVisible(false);
        this.getChildren().add(interactionImageView);
        rotationTimeline = new Timeline(new KeyFrame(Duration.seconds(0.125), e -> { // 0.125 seconds * 8 ticks = 1 second for 1 full rotation.
            interactionImageView.setRotate(interactionImageView.getRotate() + 45); // 360 degrees / 8 ticks = 45 degrees per tick
        }));
        rotationTimeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely

        if (DEBUG_SHOW_COORDINATES) {
            spaceCoords.setTextAlignment(TextAlignment.CENTER);
            spaceCoords.setText("(" + space.x + ", " + space.y + ")");
            spaceCoords.setFill(new Color(.35, .35, .35, .7));
            spaceCoords.setStyle("-fx-font-size: 20px; ");
            spaceCoords.setWrappingWidth(ApplicationSettings.SPACE_SIZE * 0.9);
            this.getChildren().add(spaceCoords);
        }

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updateSpace() {
        BoardElement boardElement = space.getBoardElement();
        this.getChildren().clear();

        // Space background imageView
        this.getChildren().add(backgroundImageView);

        // Board element imageView
        if (boardElement != null) {
            this.getChildren().add(boardElementImageView);
        }

        if (space.getCheckPoint() != null) {
            checkpointImageView.setImage(space.getCheckPoint().getImage());
            this.getChildren().add(checkpointImageView);
        }

        // Energy cube imageView
        if (boardElement instanceof BE_EnergySpace energySpace) {
            if (energySpace.getHasEnergyCube()) {
                this.getChildren().add(energyCubeImageView);
            }
        }

        // Player imageView
        Player player = space.getPlayer();
        if (player != null) {
            try {
                Image playerImage;
                if (player.getIsRebooting()) {
                    if (!usingPlayerRebootImage) {
                        usingPlayerRebootImage = true;
                        playerRebootImage = ImageUtils.getImageColored(player.getImage(), Color.BLACK, .75);
                    }
                    playerImage = playerRebootImage;
                } else {
                    playerImage = player.getImage();
                    usingPlayerRebootImage = false;
                }
                playerImageView.setImage(ImageUtils.getRotatedImageByHeading(playerImage, player.getHeading()));
            } catch (Exception e) {
                playerImageView.setImage(ImageUtils.getRotatedImageByHeading(ImageUtils.getImageFromName("Robot_Error.png"), player.getHeading()));
            }
            this.getChildren().add(playerImageView);
            this.getChildren().add(readyCheckImageView);
            this.getChildren().add(interactionImageView);
        }

        // Walls imageView
        for (ImageView wall : wallImageViews) {
            this.getChildren().add(wall);
        }

        // Lasers imageView
        for (Laser.LaserOnSpace laserOnSpace : space.getLasersOnSpace()) {
            ImageView laserImageView = newLaserImageView(laserOnSpace);
            this.getChildren().add(laserImageView);
        }

        if (DEBUG_SHOW_COORDINATES) {
            this.getChildren().add(spaceCoords);
        }
        this.getChildren().add(debugText);
    }

    private ImageView newLaserImageView(Laser.LaserOnSpace laserOnSpace) {
        ImageView laserImageView = new ImageView();
        laserImageView.setFitWidth(ApplicationSettings.SPACE_SIZE);
        laserImageView.setFitHeight(SPACE_SIZE);
        laserImageView.setImage(ImageUtils.getRotatedImageByHeading(ImageUtils.getImageFromName(laserOnSpace.getImageName()), laserOnSpace.getDirection()));
        return laserImageView;
    }

    public void updateBoardElementImage() {
        BoardElement boardElement = space.getBoardElement();
        if (boardElement != null) {
            boardElementImageView.setImage(boardElement.getImage());
        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updateSpace();
        }
    }

    public void setReadyTickVisible(boolean visible) {
        if (visible) {
            DropShadow dropShadow = new DropShadow(2, 0, 1, Color.BLACK);
            readyCheckImageView.setEffect(dropShadow);
        } else {
            readyCheckImageView.setEffect(null);
        }
        readyCheckImageView.setVisible(visible);
    }

    public void setInteractionLoadingVisible(boolean visible) {
        if (visible) {
            DropShadow dropShadow = new DropShadow(2, 0, 1, Color.BLACK);
            interactionImageView.setEffect(dropShadow);
            rotationTimeline.play();
        } else {
            interactionImageView.setEffect(null);
            rotationTimeline.stop(); // Stop rotation
            interactionImageView.setRotate(0);
        }
        interactionImageView.setVisible(visible);
    }
}
