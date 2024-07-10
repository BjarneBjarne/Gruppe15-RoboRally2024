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

import com.group15.roborally.client.RoboRally;
import com.group15.roborally.common.observer.Subject;
import com.group15.roborally.common.observer.ViewObserver;
import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.player_interaction.CommandOptionsInteraction;
import com.group15.roborally.client.utils.ImageUtils;
import com.group15.roborally.client.utils.TextUtils;
import com.group15.roborally.client.model.Player;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.group15.roborally.client.ApplicationSettings.ZOOM_SPEED;
import static com.group15.roborally.client.BoardOptions.NO_OF_CARDS_IN_HAND;
import static com.group15.roborally.common.model.GamePhase.*;
import static com.group15.roborally.client.ApplicationSettings.CARDFIELD_SIZE;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class PlayerView extends StackPane implements ViewObserver {
    @Getter
    private final Player player;

    private final CardFieldView[] programCardViews;
    
    private final HBox interactionPane = new HBox();
    private final HBox readyPanel;
    private final HBox playerOptionsPanel;

    private final Button readyButton;

    private final GameController gameController;
    private final ImageView energyCubesImageView = new ImageView();
    private final ImageView checkpointTokenImageView = new ImageView();
    private final Image[] energyCubeImages = new Image[Player.NO_OF_ENERGY_CUBES + 1];
    private final Image[] checkpointTokenImages;

    public static final double temporaryCardSize = 1;
    public static final double handCardSize = 0.8;
    public static final double permanentCardSize = 1.1;
    public static final double programCardSize = 1.165;
    public static final double programPaneYPositionOffset = 33;

    public PlayerView(@NotNull GameController gameController, double height) {
        super();
        //StackPane.setMargin(playerViewPane, new Insets(-25, 0, 27, 0));
        this.setHeight(height);
        this.gameController = gameController;
        this.player = gameController.getLocalPlayer();

        // Images
        for (int i = 0; i < energyCubeImages.length; i++) {
            energyCubeImages[i] = ImageUtils.getImageFromName("Player_Mat/EnergyCubePositions/" + i + ".png");
        }

        checkpointTokenImages = new Image[player.board.NO_OF_CHECKPOINTS + 1];
        for (int i = 0; i < checkpointTokenImages.length; i++) {
            checkpointTokenImages[i] = ImageUtils.getImageFromName("Player_Mat/CheckpointTokenPositions/" + i + ".png");
        }

        double permanentUpgradeCardsPaneOffset = CARDFIELD_SIZE * 1.35;
        double programPaneOffset = CARDFIELD_SIZE * 1.12;

        GridPane handCardsPane = new GridPane();
        for (int i = 0; i < NO_OF_CARDS_IN_HAND; i++) {
            CardField cardField = player.getCardField(i);
            if (cardField != null) {
                double cardScale = (this.getHeight() / 550) * handCardSize;
                CardFieldView cardFieldView = new CardFieldView(gameController, cardField, 1 * cardScale, 1.4 * cardScale);
                cardFieldView.setStyle(
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5"
                );
                GridPane.setMargin(cardFieldView, new Insets(2, 2, 2, 2));
                handCardsPane.add(cardFieldView, i % (NO_OF_CARDS_IN_HAND / 2), i / (NO_OF_CARDS_IN_HAND / 2));
                player.board.attach(cardFieldView);
            }
        }
        handCardsPane.setAlignment(Pos.CENTER);

        GridPane programPane = new GridPane();
        programCardViews = new CardFieldView[Player.NO_OF_REGISTERS];
        for (int i = 0; i < Player.NO_OF_REGISTERS; i++) {
            CardField cardField = player.getProgramField(i);
            if (cardField != null) {
                double cardScale = (this.getHeight() / 550) * programCardSize;
                CardFieldView cardFieldView = new CardFieldView(gameController, cardField, 1 * cardScale, 1.4 * cardScale);
                programCardViews[i] = cardFieldView;
                cardFieldView.setOnMouseEntered(_ -> cardFieldView.setTranslateY(-programPaneOffset));
                cardFieldView.setOnMouseExited(_ -> cardFieldView.setTranslateY(0));
                cardFieldView.setAlignment(Pos.CENTER);
                cardFieldView.setStyle(
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5"
                );
                GridPane.setMargin(cardFieldView, new Insets(0, 3, 0, 5));
                programPane.add(cardFieldView, i, 0);
                player.board.attach(cardFieldView);
            }
        }

        GridPane permanentUpgradeCardsPane = new GridPane();
        for (int i = 0; i < Player.NO_OF_PERMANENT_UPGRADE_CARDS; i++) {
            CardField cardField = player.getPermanentUpgradeCardField(i);
            if (cardField != null) {
                CardFieldView cardFieldView = new CardFieldView(gameController, cardField, 1 * permanentCardSize, 1.6 * permanentCardSize);
                cardFieldView.setOnMouseEntered(_ -> cardFieldView.setTranslateY(permanentUpgradeCardsPaneOffset));
                cardFieldView.setOnMouseExited(_ -> cardFieldView.setTranslateY(0));
                cardFieldView.setAlignment(Pos.CENTER);
                //cardFieldView.setStyle("-fx-background-color: transparent; ");
                cardFieldView.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-border-radius: 5; " +
                                "-fx-border-color: orange; " +
                                "-fx-border-width: 2px "
                );
                GridPane.setMargin(cardFieldView, new Insets(0, 5, 0, 5));
                permanentUpgradeCardsPane.add(cardFieldView, i, 0);
                player.board.attach(cardFieldView);
            }
        }
        permanentUpgradeCardsPane.setAlignment(Pos.CENTER);

        GridPane temporaryUpgradeCardsPane = new GridPane();
        temporaryUpgradeCardsPane.setHgap(10);
        for (int i = 0; i < Player.NO_OF_TEMPORARY_UPGRADE_CARDS; i++) {
            CardField cardField = player.getTemporaryUpgradeCardField(i);
            if (cardField != null) {
                CardFieldView cardFieldView = new CardFieldView(gameController, cardField, 1 * temporaryCardSize, 1.6 * temporaryCardSize);
                cardFieldView.setAlignment(Pos.CENTER);
                cardFieldView.setStyle(
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: #a62a24; " +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5"
                );
                GridPane.setMargin(cardFieldView, new Insets(0, 2, 0, 2));
                temporaryUpgradeCardsPane.add(cardFieldView, i, 0);
                player.board.attach(cardFieldView);
            }
        }
        temporaryUpgradeCardsPane.setAlignment(Pos.CENTER);

        // Buttons
        readyButton = new Button();
        Font readyFont = TextUtils.loadFont("OCRAEXT.TTF", 64);
        Text buttonText = new Text();
        buttonText.setFont(readyFont);
        buttonText.setFill(Color.WHITE);
        buttonText.setTextAlignment(TextAlignment.CENTER);
        buttonText.setText("Ready");
        readyButton.setGraphic(buttonText);
        readyButton.setOnAction(_ -> gameController.finishedProgramming());
        readyPanel = new HBox(readyButton);
        readyPanel.setAlignment(Pos.CENTER);
        readyPanel.setSpacing(3.0);
        readyButton.setEffect(new DropShadow(3, 0, 0, Color.BLACK));
        readyButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10; " +
                "-fx-border-color: ffffff; " +
                "-fx-border-width: 2 "
        );

        playerOptionsPanel = new HBox();
        playerOptionsPanel.setAlignment(Pos.CENTER);
        playerOptionsPanel.setSpacing(3.0);

        // Building the PlayerView structure
        // Player mat
        AnchorPane playerMatAnchorPane = new AnchorPane(permanentUpgradeCardsPane, programPane);

        AnchorPane.setTopAnchor(permanentUpgradeCardsPane, -permanentUpgradeCardsPaneOffset - 10);
        AnchorPane.setRightAnchor(permanentUpgradeCardsPane, 17.0);

        programPane.setAlignment(Pos.TOP_CENTER);
        AnchorPane.setBottomAnchor(programPane, -programPaneOffset - programPaneYPositionOffset);
        AnchorPane.setLeftAnchor(programPane, 0.0);
        AnchorPane.setRightAnchor(programPane, 1.0);

        Text playerMatCharacterText = new Text();
        AnchorPane playerMatCharacterNameAnchorPane = new AnchorPane(playerMatCharacterText);

        StackPane playerMat = new StackPane();
        playerMat.setAlignment(Pos.BOTTOM_CENTER);
        ImageView playerMatImageView = new ImageView();
        ImageView playerMatColorsImageView = new ImageView();
        ImageView playerMatCharacterImageView = new ImageView();
        playerMat.getChildren().addAll(
                playerMatImageView,
                playerMatColorsImageView,
                playerMatCharacterImageView,
                playerMatCharacterNameAnchorPane,
                energyCubesImageView,
                checkpointTokenImageView,
                playerMatAnchorPane
        );

        playerMatImageView.setImage(ImageUtils.getImageFromName("Player_Mat/PlayerMat.png"));
        playerMatImageView.setPreserveRatio(true);
        Image foregroundImage = ImageUtils.getImageFromName("Player_Mat/PlayerMatForeground.png");
        if (foregroundImage != null) {
            Color playerColor = Color.valueOf(player.getRobot().name());
            playerMatColorsImageView.setImage(ImageUtils.getImageColored(foregroundImage, playerColor, .75));
            playerMatColorsImageView.setPreserveRatio(true);
        }
        Image matCharacterImage = ImageUtils.getImageFromName("Player_Mat/PlayerMatCharacters/PlayerMat" + player.getRobot() + ".png");
        if (matCharacterImage != null) {
            playerMatCharacterImageView.setImage(matCharacterImage);
            playerMatCharacterImageView.setPreserveRatio(true);
        }
        AnchorPane.setLeftAnchor(playerMatCharacterText, 195.0);
        AnchorPane.setTopAnchor(playerMatCharacterText, 18.0);
        Font characterFont = TextUtils.loadFont("OCRAEXT.TTF", 42);
        playerMatCharacterText.setText(player.getRobot().getRobotName());
        playerMatCharacterText.setFont(characterFont);
        playerMatCharacterText.setTextAlignment(TextAlignment.CENTER);
        playerMatCharacterText.setFill(Color.WHITE);

        energyCubesImageView.setImage(energyCubeImages[0]);
        energyCubesImageView.setPreserveRatio(true);

        checkpointTokenImageView.setImage(checkpointTokenImages[0]);
        checkpointTokenImageView.setPreserveRatio(true);

        // Player mat nodes size
        Platform.runLater(() -> {
            playerMatImageView.setFitHeight(this.getHeight());
            playerMatColorsImageView.setFitHeight(this.getHeight());
            playerMatCharacterImageView.setFitHeight(this.getHeight());
            energyCubesImageView.setFitHeight(this.getHeight());
            checkpointTokenImageView.setFitHeight(this.getHeight());
        });

        // Left pane
        VBox leftSideVBox = new VBox(handCardsPane, interactionPane);
        interactionPane.setAlignment(Pos.CENTER);
        leftSideVBox.setAlignment(Pos.CENTER);
        leftSideVBox.setSpacing(15);
        AnchorPane leftSidePane = new AnchorPane(leftSideVBox);
        AnchorPane.setTopAnchor(leftSideVBox, 0.0);
        AnchorPane.setBottomAnchor(leftSideVBox, 0.0);
        AnchorPane.setRightAnchor(leftSideVBox, 0.0);
        AnchorPane.setLeftAnchor(leftSideVBox, 0.0);

        // Right pane
        temporaryUpgradeCardsPane.setAlignment(Pos.CENTER);
        AnchorPane rightSidePane = new AnchorPane(temporaryUpgradeCardsPane);
        AnchorPane.setTopAnchor(temporaryUpgradeCardsPane, 0.0);
        AnchorPane.setBottomAnchor(temporaryUpgradeCardsPane, 0.0);
        AnchorPane.setRightAnchor(temporaryUpgradeCardsPane, 0.0);
        AnchorPane.setLeftAnchor(temporaryUpgradeCardsPane, 0.0);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.getChildren().addAll(leftSidePane, playerMat, rightSidePane);
        HBox.setHgrow(leftSidePane, Priority.ALWAYS);
        HBox.setHgrow(rightSidePane, Priority.ALWAYS);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(this.widthProperty());
        clip.heightProperty().bind(this.heightProperty());
        this.setClip(clip);

        this.getChildren().add(hBox);

        player.board.attach(this);
        update(player.board);
    }

    @Override
    public void updateView(Subject subject) {
        Board board = player.board;
        if (subject == board) {
            for (int i = 0; i < Player.NO_OF_REGISTERS; i++) {
                CardFieldView cardFieldView = programCardViews[i];
                if (cardFieldView != null) {
                    if (board.getCurrentPhase() == PLAYER_ACTIVATION) {
                        if (i < board.getCurrentRegister()) {
                            cardFieldView.setBackground(CardFieldView.BG_DONE);
                        } else if (i == board.getCurrentRegister()) {
                            cardFieldView.setBackground(CardFieldView.BG_ACTIVE);
                        } else {
                            cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                        }
                    } else {
                        cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                    }
                }
            }

            if (!gameController.getIsPlayerInteracting()) {
                interactionPane.getChildren().remove(playerOptionsPanel);
                if (!interactionPane.getChildren().contains(readyPanel)) {
                    interactionPane.getChildren().add(readyPanel);
                }
                if (board.getCurrentPhase() == PROGRAMMING) {
                    readyButton.setDisable(gameController.isFinishedProgramming());
                } else {
                    readyButton.setDisable(true);
                }
            } else if (gameController.getCurrentPlayerInteraction() instanceof CommandOptionsInteraction commandOptionsInteraction) {
                interactionPane.getChildren().remove(readyPanel);
                if (!interactionPane.getChildren().contains(playerOptionsPanel)) {
                    interactionPane.getChildren().add(playerOptionsPanel);
                }

                playerOptionsPanel.getChildren().clear();
                if (commandOptionsInteraction.getPlayer() == player) {
                    List<Command> options = commandOptionsInteraction.getOptions();
                    for (Command command : options) {
                        Button optionButton = new Button(command.displayName);
                        optionButton.setOnAction(_ -> gameController.chooseCommandOption(command));
                        optionButton.setDisable(false);
                        playerOptionsPanel.getChildren().add(optionButton);
                    }
                }
            }

            energyCubesImageView.setImage(energyCubeImages[player.getEnergyCubes()]);
            checkpointTokenImageView.setImage(checkpointTokenImages[player.getCheckpoints()]);
        }
    }

}
