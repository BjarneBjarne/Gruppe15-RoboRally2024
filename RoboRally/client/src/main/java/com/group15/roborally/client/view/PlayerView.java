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

import com.group15.roborally.client.observer.Subject;
import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.player_interaction.CommandOptionsInteraction;
import com.group15.roborally.client.utils.ImageUtils;
import com.group15.roborally.client.utils.TextUtils;
import com.group15.roborally.client.model.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.group15.roborally.client.BoardOptions.NO_OF_CARDS_IN_HAND;
import static com.group15.roborally.client.model.Phase.*;
import static com.group15.roborally.client.ApplicationSettings.CARDFIELD_SIZE;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class PlayerView extends Tab implements ViewObserver {
    private final Player player;

    private final StackPane mainPlayerViewPane;

    private final GridPane cardsPane;
    private final GridPane programPane;
    private final GridPane permanentUpgradeCardsPane;
    private final GridPane temporaryUpgradeCardsPane;

    private final CardFieldView[] programCardViews;
    
    private final HBox interactionPane = new HBox();
    private final HBox executePanel;
    private final HBox playerOptionsPanel;

    private final Button finishButton;
    private final Button executeButton;
    private final Button stepButton;

    private final GameController gameController;
    private final HBox hBox = new HBox();
    private final StackPane playerMat = new StackPane();
    private final ImageView playerMatImageView = new ImageView();
    private final ImageView playerMatColorsImageView = new ImageView();
    private final ImageView playerMatCharacterImageView = new ImageView();
    private final Text playerMatCharacterText = new Text();
    private final ImageView energyCubesImageView = new ImageView();
    private final ImageView checkpointTokenImageView = new ImageView();
    private final Image[] energyCubeImages = new Image[Player.NO_OF_ENERGY_CUBES + 1];
    private final Image[] checkpointTokenImages;

    public PlayerView(@NotNull GameController gameController, @NotNull Player player) {
        super();
        mainPlayerViewPane = new StackPane();
        mainPlayerViewPane.setMinHeight(Region.USE_COMPUTED_SIZE);
        mainPlayerViewPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        mainPlayerViewPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
        mainPlayerViewPane.setAlignment(Pos.BOTTOM_CENTER);
        StackPane.setMargin(mainPlayerViewPane, new Insets(-25, 0, 27, 0));
        this.setContent(mainPlayerViewPane);
        this.gameController = gameController;
        this.player = player;
        // Setting player name
        Label playerNameLabel = new Label(player.getName());
        playerNameLabel.setTextFill(Color.valueOf(player.getRobot().toString()));
        playerNameLabel.setStyle(
                        "-fx-font-size: 26;" +
                        "-fx-font-weight: bold;"
        );
        DropShadow dropShadow = new DropShadow(2, 0, 1, Color.BLACK);
        playerNameLabel.setEffect(dropShadow);
        this.setGraphic(playerNameLabel);

        // Images
        for (int i = 0; i < energyCubeImages.length; i++) {
            energyCubeImages[i] = ImageUtils.getImageFromName("Player_Mat/EnergyCubePositions/" + i + ".png");
        }

        checkpointTokenImages = new Image[player.board.NO_OF_CHECKPOINTS + 1];
        for (int i = 0; i < checkpointTokenImages.length; i++) {
            checkpointTokenImages[i] = ImageUtils.getImageFromName("Player_Mat/CheckpointTokenPositions/" + i + ".png");
        }

        double permanentUpgradeCardsPaneOffset = CARDFIELD_SIZE * 1.32;
        double programPaneOffset = CARDFIELD_SIZE * 1.12;

        cardsPane = new GridPane();
        for (int i = 0; i < NO_OF_CARDS_IN_HAND; i++) {
            CardField cardField = player.getCardField(i);
            if (cardField != null) {
                CardFieldView cardFieldView = new CardFieldView(gameController, cardField, 1 * 0.7, 1.4 * 0.7);
                cardFieldView.setStyle(
                        "-fx-background-color: transparent; " +
                                "-fx-border-color: white; " +
                                "-fx-border-width: 1px 1px 1px 1px;" +
                                "-fx-border-radius: 5"
                );
                GridPane.setMargin(cardFieldView, new Insets(2, 2, 2, 2));
                cardsPane.add(cardFieldView, i % (NO_OF_CARDS_IN_HAND / 2), i / (NO_OF_CARDS_IN_HAND / 2));
                player.board.attach(cardFieldView);
            }
        }
        cardsPane.setAlignment(Pos.CENTER);

        programPane = new GridPane();
        programCardViews = new CardFieldView[Player.NO_OF_REGISTERS];
        for (int i = 0; i < Player.NO_OF_REGISTERS; i++) {
            CardField cardField = player.getProgramField(i);
            if (cardField != null) {
                CardFieldView cardFieldView = new CardFieldView(gameController, cardField, 1, 1.4);
                programCardViews[i] = cardFieldView;
                cardFieldView.setAlignment(Pos.CENTER);
                cardFieldView.setStyle(
                        "-fx-background-color: transparent; "
                );
                //GridPane.setHalignment(cardFieldView, HPos.CENTER);
                GridPane.setMargin(cardFieldView, new Insets(0, 2, 0, 2));
                programPane.add(cardFieldView, i, 0);
                player.board.attach(cardFieldView);
            }
        }
        programPane.setAlignment(Pos.BOTTOM_CENTER);

        permanentUpgradeCardsPane = new GridPane();
        for (int i = 0; i < Player.NO_OF_PERMANENT_UPGRADE_CARDS; i++) {
            CardField cardField = player.getPermanentUpgradeCardField(i);
            if (cardField != null) {
                CardFieldView cardFieldView = new CardFieldView(gameController, cardField, 1, 1.6);
                cardFieldView.setOnMouseEntered(e -> cardFieldView.setTranslateY(permanentUpgradeCardsPaneOffset));
                cardFieldView.setOnMouseExited(e -> cardFieldView.setTranslateY(0));
                cardFieldView.setAlignment(Pos.CENTER);
                cardFieldView.setStyle(
                        "-fx-background-color: transparent; "/* +
                                "-fx-border-color: #dfcb45; " +
                                "-fx-border-width: 2px 2px 0px 2px;" +
                                "-fx-border-radius: 5"*/
                );
                //GridPane.setHalignment(cardFieldView, HPos.CENTER);
                GridPane.setMargin(cardFieldView, new Insets(0, 2, 0, 2));
                permanentUpgradeCardsPane.add(cardFieldView, i, 0);
                player.board.attach(cardFieldView);
            }
        }
        permanentUpgradeCardsPane.setAlignment(Pos.CENTER);

        temporaryUpgradeCardsPane = new GridPane();
        for (int i = 0; i < Player.NO_OF_TEMPORARY_UPGRADE_CARDS; i++) {
            CardField cardField = player.getTemporaryUpgradeCardField(i);
            if (cardField != null) {
                CardFieldView cardFieldView = new CardFieldView(gameController, cardField, 1 * 1.15, 1.6 * 1.15);
                cardFieldView.setAlignment(Pos.CENTER);
                cardFieldView.setStyle(
                        "-fx-background-color: transparent; " +
                                "-fx-border-color: #a62a24; " +
                                "-fx-border-width: 2px 2px 2px 2px;" +
                                "-fx-border-radius: 5"
                );
                //GridPane.setHalignment(cardFieldView, HPos.CENTER);
                GridPane.setMargin(cardFieldView, new Insets(0, 2, 0, 2));
                temporaryUpgradeCardsPane.add(cardFieldView, i, 0);
                player.board.attach(cardFieldView);
            }
        }
        temporaryUpgradeCardsPane.setAlignment(Pos.CENTER);
        temporaryUpgradeCardsPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        // Buttons
        // TODO: finishButton, executeButton & stepButton should be converted to a "Ready" button, when networking is implemented.
        finishButton = new Button("Finish Programming");
        finishButton.setOnAction( e -> gameController.finishProgrammingPhase());
        executeButton = new Button("Execute Program");
        executeButton.setOnAction( e-> gameController.executePrograms());
        stepButton = new Button("Execute Current Register");
        stepButton.setOnAction( e-> gameController.executeRegister());
        executePanel = new HBox(finishButton, executeButton, stepButton);
        executePanel.setAlignment(Pos.CENTER);
        executePanel.setSpacing(3.0);
        playerOptionsPanel = new HBox();
        playerOptionsPanel.setAlignment(Pos.CENTER);
        playerOptionsPanel.setSpacing(3.0);

        // Building the PlayerView structure
        // Player mat
        AnchorPane playerMatAnchorPane = new AnchorPane(permanentUpgradeCardsPane, programPane);

        AnchorPane.setTopAnchor(permanentUpgradeCardsPane, -permanentUpgradeCardsPaneOffset + 30);
        AnchorPane.setRightAnchor(permanentUpgradeCardsPane, 0.0);

        programPane.setAlignment(Pos.BOTTOM_CENTER);
        AnchorPane.setBottomAnchor(programPane, -programPaneOffset);
        AnchorPane.setLeftAnchor(programPane, 0.0);
        AnchorPane.setRightAnchor(programPane, 0.0);

        AnchorPane playerMatCharacterNameAnchorPane = new AnchorPane(playerMatCharacterText);

        playerMat.setAlignment(Pos.BOTTOM_CENTER);
        playerMat.getChildren().addAll(playerMatImageView, playerMatColorsImageView, playerMatCharacterImageView, playerMatCharacterNameAnchorPane, energyCubesImageView, checkpointTokenImageView, playerMatAnchorPane);

        playerMatImageView.setImage(ImageUtils.getImageFromName("Player_Mat/PlayerMat.png"));
        playerMatImageView.setFitHeight(255.5 * CARDFIELD_SIZE * 0.01);
        playerMatImageView.setPreserveRatio(true);
        Image foregroundImage = ImageUtils.getImageFromName("Player_Mat/PlayerMatForeground.png");
        if (foregroundImage != null) {
            Color playerColor = Color.valueOf(player.getRobot().name());
            playerMatColorsImageView.setImage(ImageUtils.getImageColored(foregroundImage, playerColor, .75));
            playerMatColorsImageView.setFitHeight(255.5 * CARDFIELD_SIZE * 0.01);
            playerMatColorsImageView.setPreserveRatio(true);
        }
        Image matCharacterImage = ImageUtils.getImageFromName("Player_Mat/PlayerMatCharacters/PlayerMat" + player.getRobot() + ".png");
        if (matCharacterImage != null) {
            playerMatCharacterImageView.setImage(matCharacterImage);
            playerMatCharacterImageView.setFitHeight(255.5 * CARDFIELD_SIZE * 0.01);
            playerMatCharacterImageView.setPreserveRatio(true);
        }
        AnchorPane.setLeftAnchor(playerMatCharacterText, 135.0);
        AnchorPane.setTopAnchor(playerMatCharacterText, 43.0);
        playerMatCharacterText.setText(player.getRobot().getRobotName());
        Font textFont = TextUtils.loadFont("OCRAEXT.TTF", 32);
        playerMatCharacterText.setFont(textFont);
        playerMatCharacterText.setTextAlignment(TextAlignment.RIGHT);
        playerMatCharacterText.setFill(Color.WHITE);

        energyCubesImageView.setImage(energyCubeImages[0]);
        energyCubesImageView.setFitHeight(255.5 * CARDFIELD_SIZE * 0.01);
        energyCubesImageView.setPreserveRatio(true);

        checkpointTokenImageView.setImage(checkpointTokenImages[0]);
        checkpointTokenImageView.setFitHeight(255.5 * CARDFIELD_SIZE * 0.01);
        checkpointTokenImageView.setPreserveRatio(true);

        // Right side
        VBox leftSideVBox = new VBox(cardsPane, interactionPane);
        temporaryUpgradeCardsPane.setAlignment(Pos.CENTER);
        interactionPane.setAlignment(Pos.CENTER);
        leftSideVBox.setAlignment(Pos.CENTER);
        leftSideVBox.setSpacing(15);
        StackPane leftSideStackPane = new StackPane(leftSideVBox);
        leftSideStackPane.setAlignment(Pos.CENTER);

        // Padding
        HBox.setMargin(leftSideStackPane, new Insets(40, 0, 0, 150)); // Top, right, bottom, left
        HBox.setMargin(temporaryUpgradeCardsPane, new Insets(40, 150, 10, 0)); // Top, right, bottom, left

        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.getChildren().addAll(leftSideStackPane, playerMat, temporaryUpgradeCardsPane);
        hBox.setSpacing(100);
        mainPlayerViewPane.getChildren().addAll(hBox);

        if (player.board != null) {
            player.board.attach(this);
            update(player.board);
        }
    }

    @Override
    public void updateView(Subject subject) {
        Board board = player.board;
        if (subject == board) {
            for (int i = 0; i < Player.NO_OF_REGISTERS; i++) {
                CardFieldView cardFieldView = programCardViews[i];
                if (cardFieldView != null && board.getCurrentPhase() == PLAYER_ACTIVATION) {
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

            if (!gameController.getIsPlayerInteracting()) {
                interactionPane.getChildren().remove(playerOptionsPanel);
                if (!interactionPane.getChildren().contains(executePanel)) {
                    interactionPane.getChildren().add(executePanel);
                }
                switch (board.getCurrentPhase()) {
                    case PROGRAMMING:
                        finishButton.setDisable(false);
                        executeButton.setDisable(true);
                        stepButton.setDisable(true);
                        break;
                    case PLAYER_ACTIVATION, BOARD_ACTIVATION:
                        finishButton.setDisable(true);
                        executeButton.setDisable(gameController.getIsRegisterPlaying());
                        stepButton.setDisable(gameController.getIsRegisterPlaying());
                        break;
                    default:
                        finishButton.setDisable(true);
                        executeButton.setDisable(true);
                        stepButton.setDisable(true);
                }
            } else if (gameController.getCurrentPlayerInteraction() instanceof CommandOptionsInteraction commandOptionsInteraction) {
                interactionPane.getChildren().remove(executePanel);
                if (!interactionPane.getChildren().contains(playerOptionsPanel)) {
                    interactionPane.getChildren().add(playerOptionsPanel);
                }

                playerOptionsPanel.getChildren().clear();
                if (commandOptionsInteraction.player == player) {
                    List<Command> options = commandOptionsInteraction.getOptions();
                    for (Command command : options) {
                        Button optionButton = new Button(command.displayName);
                        optionButton.setOnAction(e -> gameController.executeCommandOptionAndContinue(command));
                        optionButton.setDisable(false);
                        playerOptionsPanel.getChildren().add(optionButton);
                    }
                }
            }

            energyCubesImageView.setImage(energyCubeImages[player.getEnergyCubes()]);
            checkpointTokenImageView.setImage(checkpointTokenImages[player.getCheckpoints()]);
        }
    }

    public Player getPlayer() {
        return player;
    }
}
