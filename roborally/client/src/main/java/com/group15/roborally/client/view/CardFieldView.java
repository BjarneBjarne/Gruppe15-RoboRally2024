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
import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCard;
import com.group15.roborally.client.utils.ImageUtils;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.utils.TextUtils;
import com.group15.roborally.common.model.GamePhase;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static com.group15.roborally.client.BoardOptions.NO_OF_CARDS_IN_HAND;
import static com.group15.roborally.client.ApplicationSettings.*;

/**
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class CardFieldView extends StackPane implements ViewObserver {
    // This data format helps avoiding transfers of e.g. Strings from other programs which can copy/paste Strings.
    final public static DataFormat ROBO_RALLY_CARD = new DataFormat("games/roborally/cards");

    //final public static Border BORDER = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2)));

    final public static Background BG_DEFAULT = new Background(new BackgroundFill(Color.TRANSPARENT, null, null));
    final public static Background BG_DRAG = new Background(new BackgroundFill(new Color(122 / 255.0, 119 / 255.0, 110 / 255.0, 1), null, null));
    final public static Background BG_DROP = new Background(new BackgroundFill(new Color(198 / 255.0, 194 / 255.0, 179 / 255.0, .5), null, null));

    final public static Background BG_ACTIVE = new Background(new BackgroundFill(new Color(234 / 255.0, 209 / 255.0, 87 / 255.0, .5), null, null));
    final public static Background BG_DONE = new Background(new BackgroundFill(new Color(120 / 255.0, 209 / 255.0, 87 / 255.0, .5),  null, null));

    private final CardField cardField;

    private final GameController gameController;
    @Getter
    private final ImageView cardImageView = new ImageView();
    @Getter
    private final ImageView cardForegroundImageView = new ImageView();
    private final Button useButton = new Button();
    private double cardWidthMultiplier;
    private double cardHeightMultiplier;
    private Background currentBackground;

    public CardFieldView(@NotNull GameController gameController, @NotNull CardField cardField, double cardWidthMultiplier, double cardHeightMultiplier) {
        this.gameController = gameController;
        this.cardField = cardField;
        this.setMouseTransparent(false);
        cardImageView.setMouseTransparent(true);
        cardForegroundImageView.setMouseTransparent(true);
        this.cardWidthMultiplier = cardWidthMultiplier;
        this.cardHeightMultiplier = cardHeightMultiplier;
        this.setCardBackground(BG_DEFAULT);
        updateCardSize();

        useButton.setOnAction(a -> cardField.activateCard());
        useButton.setDisable(true);
        useButton.setVisible(false);
        Font textFont = TextUtils.loadFont("OCRAEXT.TTF", 28);
        Text buttonText = new Text();
        buttonText.setFont(textFont);
        buttonText.setFill(Color.WHITE);
        buttonText.setTextAlignment(TextAlignment.CENTER);
        buttonText.setText("Use");
        useButton.setGraphic(buttonText);
        StackPane.setAlignment(useButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(useButton, new Insets(0, 0, 7, 0));
        useButton.setPadding(new Insets(3, 20, 3, 20));
        useButton.setEffect(new DropShadow(2, 0, 0, Color.BLACK));
        useButton.setStyle("-fx-background-color: transparent;" +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5; " +
                "-fx-border-color:  rgb(0,0,0);" +
                "-fx-border-width: 1 ");

        //this.setPrefSize((CARDFIELD_SIZE - 5) * cardWidthMultiplier, (CARDFIELD_SIZE - 3) * cardHeightMultiplier);
        this.getChildren().addAll(cardImageView, cardForegroundImageView, useButton);

        this.setAlignment(Pos.CENTER);
        //this.setPadding(new Insets(5, 5, 5, 5));

        this.setOnDragDetected(new OnDragDetectedHandler());
        this.setOnDragOver(new OnDragOverHandler());
        this.setOnDragEntered(new OnDragEnteredHandler());
        this.setOnDragExited(new OnDragExitedHandler());
        this.setOnDragDropped(new OnDragDroppedHandler());
        this.setOnDragDone(new OnDragDoneHandler());

        cardField.attach(this);
        update(cardField);
    }

    public void setSizeMultipliers(double cardWidthMultiplier, double cardHeightMultiplier) {
        this.cardWidthMultiplier = cardWidthMultiplier;
        this.cardHeightMultiplier = cardHeightMultiplier;
    }

    private void updateCardSize() {
        Platform.runLater(() -> {
            this.setMinSize(CARDFIELD_SIZE * cardWidthMultiplier, CARDFIELD_SIZE * cardHeightMultiplier);
            this.setPrefSize(CARDFIELD_SIZE * cardWidthMultiplier, CARDFIELD_SIZE * cardHeightMultiplier);
            this.setMaxSize(CARDFIELD_SIZE * cardWidthMultiplier, CARDFIELD_SIZE * cardHeightMultiplier);
            cardImageView.setFitWidth((CARDFIELD_SIZE - 10) * cardWidthMultiplier);
            cardImageView.setFitHeight((CARDFIELD_SIZE - 7) * cardHeightMultiplier);
            cardForegroundImageView.setFitWidth((CARDFIELD_SIZE - 10) * cardWidthMultiplier);
            cardForegroundImageView.setFitHeight((CARDFIELD_SIZE - 7) * cardHeightMultiplier);
            double offset = this.getWidth() / 65;
            cardImageView.setTranslateX(offset);
            cardImageView.setTranslateY(offset);
            cardForegroundImageView.setTranslateX(offset);
            cardForegroundImageView.setTranslateY(offset);
        });
    }

    public String cardFieldRepresentation(CardField cardField) {
        if (cardField.player != null) {
            for (int i = 0; i < Player.NO_OF_REGISTERS; i++) {
                CardField other = cardField.player.getProgramField(i);
                if (other == cardField) {
                    return "P," + i;    // Program cards
                }
            }
            for (int i = 0; i < NO_OF_CARDS_IN_HAND; i++) {
                CardField other = cardField.player.getCardField(i);
                if (other == cardField) {
                    return "C," + i;    // Cards in hand
                }
            }
            for (int i = 0; i < Player.NO_OF_PERMANENT_UPGRADE_CARDS; i++) {
                CardField other = cardField.player.getPermanentUpgradeCardField(i);
                if (other == cardField) {
                    return "U," + i;    // Permanent player upgrade cardField
                }
            }
            for (int i = 0; i < Player.NO_OF_TEMPORARY_UPGRADE_CARDS; i++) {
                CardField other = cardField.player.getTemporaryUpgradeCardField(i);
                if (other == cardField) {
                    return "T," + i;    // Temporary player upgrade cardField
                }
            }
        } else {
            if (gameController.board.getCurrentPhase().equals(GamePhase.UPGRADE)) {
                if (cardField.cardFieldType == CardField.CardFieldTypes.UPGRADE_CARD_SHOP_FIELD) {
                    UpgradeShop upgradeShop = gameController.board.getUpgradeShop();
                    for (int i = 0; i < upgradeShop.getAvailableCardsFields().length; i++) {
                        CardField other = upgradeShop.getAvailableCardsField(i);
                        if (other == cardField) {
                            return "S," + i;    // Shop cardField
                        }
                    }
                }
            }
        }
        return null;
    }

    public CardField cardFieldFromRepresentation(String rep) {
        if (rep != null) {
            //System.out.println("Checking for card with representation: \"" + rep + "\"");
            String[] strings = rep.split(",");
            if (strings.length == 2) {
                int i = Integer.parseInt(strings[1]);
                if (cardField.player != null) {
                    switch (strings[0]) {
                        case "P" -> {
                            if (i < Player.NO_OF_REGISTERS) {
                                return cardField.player.getProgramField(i);
                            }
                        }
                        case "C" -> {
                            if (i < NO_OF_CARDS_IN_HAND) {
                                return cardField.player.getCardField(i);
                            }
                        }
                        case "U" -> {
                            if (i < Player.NO_OF_PERMANENT_UPGRADE_CARDS) {
                                return cardField.player.getPermanentUpgradeCardField(i);
                            }
                        }
                        case "T" -> {
                            if (i < Player.NO_OF_TEMPORARY_UPGRADE_CARDS) {
                                return cardField.player.getTemporaryUpgradeCardField(i);
                            }
                        }
                    }
                } else {
                    if (strings[0].equals("S")) {
                        if (i < gameController.board.getUpgradeShop().getAvailableCardsFields().length) {
                            return gameController.board.getUpgradeShop().getAvailableCardsField(i);
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void updateView(Subject subject) {
        if ((subject == cardField || subject == cardField.player.board) && subject != null) {
            Card card = cardField.getCard();
            useButton.setDisable(true);
            useButton.setVisible(false);
            if (card != null && cardField.isVisible()) {
                useButton.setDisable(!cardField.getCanBeActivated());
                useButton.setVisible(cardField.getHasActivateButton());

                String cardName = card.getDisplayName();
                String cardImageName;
                String cardFolderPath = "";
                if (card instanceof CommandCard commandCard) {
                    cardImageName = cardName + ".png";
                    cardFolderPath = "Cards/Programming_Cards/";
                    if (commandCard.command.isNormalProgramCommand()) {
                        Image cardForegroundImage = ImageUtils.getImageFromName(cardFolderPath + "Foregrounds/" + cardImageName);
                        if (cardForegroundImage != null && cardField.player != null) {
                            Color playerColor = Color.valueOf(cardField.player.getRobot().name());
                            Image playerColoredImage = ImageUtils.getImageColored(cardForegroundImage, playerColor, .75);
                            Color backgroundColor = (Color)(currentBackground.getFills().getFirst().getFill());
                            if (backgroundColor.equals(Color.TRANSPARENT)) backgroundColor = Color.WHITE;
                            Image imageWithBackgroundColor = ImageUtils.getImageColored(playerColoredImage, backgroundColor, 1);
                            cardForegroundImageView.setImage(imageWithBackgroundColor);
                        }
                    }
                } else if (card instanceof UpgradeCard) {
                    cardImageName =  cardName.toUpperCase() + ".png";
                    cardFolderPath = "Cards/Upgrade_Cards/";
                } else {
                    cardImageName = "Card_Error.png";
                }
                Image currentImage = ImageUtils.getImageFromName(cardFolderPath + cardImageName);
                if (currentImage != null) {
                    Color backgroundColor = (Color)(currentBackground.getFills().getFirst().getFill());
                    if (backgroundColor.equals(Color.TRANSPARENT)) backgroundColor = Color.WHITE;
                    Image imageWithBackgroundColor = ImageUtils.getImageColored(currentImage, backgroundColor, 1);
                    cardImageView.setImage(imageWithBackgroundColor);
                }

            } else {
                cardImageView.setImage(null);
                cardForegroundImageView.setImage(null);
            }
        }
    }

    private Pair<CardField, CardFieldView> getCardFieldsFromDragEvent(DragEvent event) {
        CardField sourceField = null;
        CardFieldView targetFieldView = null;

        Object t = event.getTarget();
        if (t instanceof CardFieldView target) {
            Dragboard dragboard = event.getDragboard();
            if (event.getGestureSource() instanceof CardFieldView sourceCardFieldView) {
                if (sourceCardFieldView != target && dragboard.hasContent(ROBO_RALLY_CARD)) {
                    Object object = dragboard.getContent(ROBO_RALLY_CARD);
                    if (object instanceof String) {
                        sourceField = sourceCardFieldView.cardFieldFromRepresentation((String) object);
                        targetFieldView = target;
                    }
                }
            }
        }

        return new Pair<>(sourceField, targetFieldView);
    }

    private class OnDragDetectedHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView source) {
                CardField sourceField = source.cardField;
                if (gameController.canDragCard(sourceField)) {
                    // Getting resized image of the CardField
                    SnapshotParameters params = new SnapshotParameters();
                    params.setFill(Color.TRANSPARENT);
                    Image originalImage = source.snapshot(params, null);
                    ImageView imageView = new ImageView(originalImage);
                    Bounds boundsInScene = source.cardImageView.localToScene(source.cardImageView.getBoundsInLocal());
                    imageView.setFitWidth(boundsInScene.getWidth());
                    imageView.setFitHeight(boundsInScene.getHeight());
                    Image resizedImage = imageView.snapshot(params, null);

                    // Setting image to the Dragboard
                    Dragboard dragboard = source.startDragAndDrop(TransferMode.MOVE);
                    dragboard.setDragView(resizedImage);

                    // Getting offset from CardField to mouse
                    double offsetX = (event.getSceneX() - boundsInScene.getMinX());
                    double offsetY = (event.getSceneY() - boundsInScene.getMinY());
                    dragboard.setDragViewOffsetX(offsetX);
                    dragboard.setDragViewOffsetY(offsetY);

                    /*double scaleX = resizedImage.getWidth() / originalImage.getWidth();
                    double scaleY = resizedImage.getHeight() / originalImage.getHeight();
                    Bounds localBounds = source.getBoundsInLocal();
                    Bounds sceneBounds = source.localToScene(localBounds);
                    double offsetX = (event.getSceneX() - sceneBounds.getMinX()) * scaleX;
                    double offsetY = (event.getSceneY() - sceneBounds.getMinY()) * scaleY;
                    dragboard.setDragViewOffsetX(offsetX);
                    dragboard.setDragViewOffsetY(offsetY);*/

                    // Saving representation from card in the Dragboard
                    ClipboardContent content = new ClipboardContent();
                    String representation = cardFieldRepresentation(sourceField);
                    content.put(ROBO_RALLY_CARD, representation);
                    //System.out.println("Made representation: \"" + representation + "\" for cardField with card " + sourceField.getCard().getName());

                    dragboard.setContent(content);

                    source.setCardBackground(BG_DRAG);
                }
            }
            event.consume();
        }
    }

    private class OnDragOverHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            Pair<CardField, CardFieldView> sourceAndTargetCardFields = getCardFieldsFromDragEvent(event);
            CardFieldView targetFieldView = sourceAndTargetCardFields.getValue();
            CardField sourceField = sourceAndTargetCardFields.getKey();

            //System.out.println(sourceField);

            if (targetFieldView != null) {
                if (gameController.canDropCard(sourceField, targetFieldView.cardField)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            }

            event.consume();
        }
    }

    private class OnDragEnteredHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            Pair<CardField, CardFieldView> sourceAndTargetCardFields = getCardFieldsFromDragEvent(event);
            CardFieldView targetFieldView = sourceAndTargetCardFields.getValue();
            CardField sourceField = sourceAndTargetCardFields.getKey();

            if (targetFieldView != null) {
                if (gameController.canDropCard(sourceField, targetFieldView.cardField)) {
                    targetFieldView.setCardBackground(BG_DROP);
                }
            }

            event.consume();
        }
    }

    private class OnDragExitedHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            Pair<CardField, CardFieldView> sourceAndTargetCardFields = getCardFieldsFromDragEvent(event);
            CardFieldView targetFieldView = sourceAndTargetCardFields.getValue();

            if (targetFieldView != null) {
                targetFieldView.setCardBackground(BG_DEFAULT);
            }

            event.consume();
        }
    }

    private class OnDragDroppedHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            Pair<CardField, CardFieldView> sourceAndTargetCardFields = getCardFieldsFromDragEvent(event);
            CardFieldView targetFieldView = sourceAndTargetCardFields.getValue();
            CardField sourceField = sourceAndTargetCardFields.getKey();

            if (targetFieldView != null) {
                CardField targetField = targetFieldView.cardField;

                boolean success = false;
                if (gameController.canDropCard(sourceField, targetField)) {
                    gameController.moveCard(sourceField, targetField);
                    success = true;
                }
                event.setDropCompleted(success);
                targetFieldView.setCardBackground(BG_DEFAULT);

                if (sourceField.cardFieldType == CardField.CardFieldTypes.UPGRADE_CARD_SHOP_FIELD) {
                    if (event.getGestureSource() instanceof CardFieldView sourceCardFieldView) {
                        //System.out.println("Editing source: " + sourceCardFieldView.field.index);
                        sourceCardFieldView.setStyle(
                                "-fx-background-color: transparent; " +
                                "-fx-border-color: transparent; "
                        );
                    }
                }
            }

            event.consume();
        }
    }

    private static class OnDragDoneHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView source) {
                source.setCardBackground(BG_DEFAULT);
            }
            event.consume();
        }
    }

    public void setCardBackground(Background background) {
        this.setBackground(background);
        currentBackground = background;
        cardField.updateCardField();
    }
}
