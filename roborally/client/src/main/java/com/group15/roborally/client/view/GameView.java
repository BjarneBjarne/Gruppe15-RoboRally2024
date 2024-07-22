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
 */
package com.group15.roborally.client.view;

import com.group15.roborally.common.observer.Subject;
import com.group15.roborally.common.observer.ViewObserver;
import com.group15.roborally.client.ApplicationSettings;
import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardPermanent;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardTemporary;
import com.group15.roborally.client.utils.ImageUtils;
import com.group15.roborally.client.utils.TextUtils;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.jetbrains.annotations.NotNull;

import static com.group15.roborally.client.ApplicationSettings.*;
import static com.group15.roborally.client.BoardOptions.NO_OF_PLAYERS;
import com.group15.roborally.common.model.GamePhase;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for making the main JavaFX nodes for gameplay.
 * This includes the view of the board, along with the views for each of its spaces, as well as the view containing -
 *     the player mats.
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class GameView extends AnchorPane implements ViewObserver {
    private final GameController gameController;
    private final Board board;

    private final StackPane mainBoardPane;
    private final SpaceView[][] spaceViews;
    private final GridPane directionOptionsPane;
    private StackPane upgradeShopPane;
    private HBox upgradeShopCardsHBox;
    private Text otherPlayerTurnText;
    private Button finishUpgradingButton;
    private final StackPane upgradeShopParentPane;
    private final ImageView cardShowcaseImageView;
    //private final Label statusLabel;

    /**
     * Constructor of GameView.
     * @param gameController The GameController.
     * @param directionOptionsPane The loaded directionOptionsPane from the DirectionArrows.fxml file.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public GameView(@NotNull GameController gameController, GridPane directionOptionsPane) {
        this.gameController = gameController;
        this.directionOptionsPane = directionOptionsPane;
        this.board = gameController.board;
        this.board.initializeUpgradeShop();
        this.spaceViews = new SpaceView[board.width][board.height];

        // Direction options pane
        this.directionOptionsPane.setPrefSize(ApplicationSettings.SPACE_SIZE * 3, SPACE_SIZE * 3);
        List<Node> children = this.directionOptionsPane.getChildren();
        for (Node child : children) {
            if (child instanceof Button button) {
                initializeDirectionButton(button, this);
                ImageView buttonImage = new ImageView();
                buttonImage.setFitWidth(ApplicationSettings.SPACE_SIZE);
                buttonImage.setFitHeight(SPACE_SIZE);
                Heading direction = Heading.valueOf(button.getId());
                buttonImage.setImage(ImageUtils.getRotatedImageByHeading(ImageUtils.getImageFromName("arrow.png"), direction));
                button.setGraphic(buttonImage);
            }
        }
        this.directionOptionsPane.setDisable(true);
        this.directionOptionsPane.setVisible(false);

        // Pane containing the actual board tiles
        GridPane boardTilesPane = new GridPane();
        boardTilesPane.setAlignment(Pos.CENTER);
        StackPane.setAlignment(boardTilesPane, Pos.CENTER);

        // Anchor pane to position the directionOptionsPane
        AnchorPane directionOptionsAnchorPane = new AnchorPane(directionOptionsPane);
        StackPane.setAlignment(directionOptionsAnchorPane, Pos.CENTER);

        // Interactable pane
        StackPane interactablePane = new StackPane(boardTilesPane, directionOptionsAnchorPane);
        interactablePane.getStyleClass().add("transparent-scroll-pane");
        interactablePane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0), CornerRadii.EMPTY, null)));
        interactablePane.setStyle(
                "-fx-background: transparent; " +
                "-fx-background-color: transparent; "
        );

        // ZoomableScrollPane
        ZoomableScrollPane zoomableScrollPane = new ZoomableScrollPane(interactablePane, 0.9);
        zoomableScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        zoomableScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        zoomableScrollPane.getStyleClass().add("transparent-scroll-pane");
        zoomableScrollPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0), CornerRadii.EMPTY, null)));
        zoomableScrollPane.setStyle(
                "-fx-background: transparent; " +
                "-fx-background-color: transparent; "
        );
        zoomableScrollPane.setPannable(true);

        // Upgrade card showcase
        cardShowcaseImageView = new ImageView();
        cardShowcaseImageView.setVisible(false);
        cardShowcaseImageView.setOpacity(0.9);
        cardShowcaseImageView.setMouseTransparent(true);
        double cardShowcaseWidth = 400;
        cardShowcaseImageView.setFitWidth(cardShowcaseWidth);
        cardShowcaseImageView.setFitHeight(cardShowcaseWidth * 1.6);
        StackPane cardShowcaseStackPane = new StackPane(cardShowcaseImageView);
        cardShowcaseStackPane.setMouseTransparent(true);

        double playerViewHeight = 550;
        upgradeShopParentPane = new StackPane();

        // Main board pane
        mainBoardPane = new StackPane(zoomableScrollPane, upgradeShopParentPane, cardShowcaseStackPane);
        this.heightProperty().addListener((_, _, _) -> {
            mainBoardPane.setMinSize(REFERENCE_WIDTH, this.getHeight() - playerViewHeight - 1);
            mainBoardPane.setPrefSize(REFERENCE_WIDTH, this.getHeight() - playerViewHeight - 1);
            mainBoardPane.setMaxSize(REFERENCE_WIDTH, this.getHeight() - playerViewHeight - 1);
        });
        mainBoardPane.setAlignment(Pos.TOP_CENTER);
        mainBoardPane.getStyleClass().add("transparent-scroll-pane");
        mainBoardPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0), CornerRadii.EMPTY, null)));
        mainBoardPane.setStyle(
                "-fx-background: transparent; " +
                "-fx-background-color: transparent; "
        );

        // PlayerView
        PlayerView playerView = new PlayerView(cardShowcaseImageView, gameController, playerViewHeight);
        playerView.setMinHeight(playerViewHeight);
        playerView.setPrefHeight(playerViewHeight);
        playerView.setMaxHeight(playerViewHeight);
        playerView.setAlignment(Pos.BOTTOM_CENTER);
        playerView.setStyle(
                "-fx-background: transparent; " +
                "-fx-background-color: transparent; "
        );

        // Space views
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                if (space == null) continue;
                SpaceView spaceView = new SpaceView(space);
                spaceViews[x][y] = spaceView;
                boardTilesPane.add(spaceView, x, y);
            }
        }

        // Size of draggable pane (interactablePane)
        double boardPaneWidth = boardTilesPane.getWidth() + 12000;
        double boardPaneHeight = boardPaneWidth / 2.72;
        interactablePane.setMinSize(boardPaneWidth, boardPaneHeight);
        interactablePane.setPrefSize(boardPaneWidth, boardPaneHeight);
        interactablePane.setMaxSize(boardPaneWidth, boardPaneHeight);

        // SpaceEventHandler
        SpaceEventHandler spaceEventHandler = new SpaceEventHandler(gameController);
        mainBoardPane.setOnMouseClicked(spaceEventHandler);

        //statusLabel = new Label("<no status>");

        // Adding panes to the game pane
        this.getChildren().addAll(playerView, mainBoardPane);
        //this.getChildren().add(statusLabel);

        AnchorPane.setTopAnchor(mainBoardPane, 0.0);
        AnchorPane.setLeftAnchor(mainBoardPane, 0.0);
        AnchorPane.setRightAnchor(mainBoardPane, 0.0);

        AnchorPane.setBottomAnchor(playerView, 0.0);
        AnchorPane.setLeftAnchor(playerView, 0.0);
        AnchorPane.setRightAnchor(playerView, 0.0);

        AnchorPane.setTopAnchor(cardShowcaseStackPane, 0.0);
        AnchorPane.setBottomAnchor(cardShowcaseStackPane, 100.0);
        AnchorPane.setLeftAnchor(cardShowcaseStackPane, 0.0);
        AnchorPane.setRightAnchor(cardShowcaseStackPane, 0.0);

        board.attach(this);
        update(board);
    }

    /**
     * Sets the onMouseClicked up the arrow buttons on the direction panel, to call chooseDirection().
     * @param button The arrow button.
     * @param gameView The gameView
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void initializeDirectionButton(Button button, GameView gameView) {
        Heading direction = Heading.valueOf(button.getId());
        button.setOnMouseClicked(_ -> gameController.chooseDirection(direction, gameView));
    }

    /**
     * Method for initializing the upgrade shop. Should only be called once after creating a GameView.
     * @param upgradeShopPane Root of the upgrade shop.
     * @param upgradeShopTitelPane The upper pane, containing the title.
     * @param upgradeShopMainPane The main pane containing the cards and finish button.
     * @param upgradeShopCardsHBox The HBox to put the card views in.
     * @param finishUpgradingButton The button for exiting the shop.
     *
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setUpgradeShopFXML(StackPane upgradeShopPane, StackPane upgradeShopTitelPane, StackPane upgradeShopMainPane, HBox upgradeShopCardsHBox, Button finishUpgradingButton) {
        this.upgradeShopPane = upgradeShopPane;
        this.upgradeShopCardsHBox = upgradeShopCardsHBox;
        this.finishUpgradingButton = finishUpgradingButton;

        upgradeShopParentPane.getChildren().add(this.upgradeShopPane);
        StackPane.setAlignment(this.upgradeShopPane, Pos.CENTER);

        if (this.upgradeShopCardsHBox == null) {
            System.out.println("upgradeShopCardsHBox not initialized in GameView - setUpgradeShopFXML()");
        }
        if (this.finishUpgradingButton == null) {
            System.out.println("finishUpgradingButton not initialized in GameView - setUpgradeShopFXML()");
        } else {
            this.finishUpgradingButton.setOnMouseClicked(_ -> {
                gameController.setPlayerCards();
                this.finishUpgradingButton.setVisible(false);
                this.finishUpgradingButton.setDisable(true);
                this.finishUpgradingButton.setMouseTransparent(true);
            });

            // Button text
            Font textFont = TextUtils.loadFont("OCRAEXT.TTF", 42);
            Text buttonText = getOtherPlayerTurnText(textFont);
            buttonText.setText("Finish Upgrading");
            this.finishUpgradingButton.setGraphic(buttonText);
        }

        upgradeShopTitelPane.setStyle(
                "-fx-background-color: rgba(0,0,0,.5); " +
                "-fx-background-radius: 15px"
        );
        upgradeShopMainPane.setStyle(
                "-fx-background-color: rgba(0,0,0,.5); " +
                        "-fx-background-radius: 15px"
        );
        Font textFont = TextUtils.loadFont("OCRAEXT.TTF", 48);
        otherPlayerTurnText = getOtherPlayerTurnText(textFont);
        this.upgradeShopPane.getChildren().add(otherPlayerTurnText);
        otherPlayerTurnText.setMouseTransparent(true);

        /*double height = 800 * 0.75;
        this.upgradeShopPane.setMinHeight(height);
        this.upgradeShopPane.setPrefHeight(height);
        this.upgradeShopPane.setMaxHeight(height);*/
        this.upgradeShopPane.setScaleX(0.75);
        this.upgradeShopPane.setScaleY(0.75);
        this.upgradeShopParentPane.setMouseTransparent(true);
    }

    /**
     * Method for refreshing the card views, to show the new cards that are available for purchase.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void updateUpgradeShop() {
        upgradeShopCardsHBox.getChildren().clear();
        UpgradeShop upgradeShop = board.getUpgradeShop();
        Player playerUpgrading = gameController.getPlayerUpgrading();

        for (int i = 0; i < NO_OF_PLAYERS; i++) {
            CardField cardField = upgradeShop.getAvailableCardsField(i);
            CardFieldView cardFieldView = new CardFieldView(gameController, cardField, 1 * 1.5, 1.6 * 1.5);
            boolean localPlayersTurn = gameController.getLocalPlayer().equals(playerUpgrading);
            upgradeShopCardsHBox.getChildren().add(cardFieldView);
            cardFieldView.setAlignment(Pos.CENTER);
            switch (cardField.getCard()) {
                case UpgradeCardPermanent _, UpgradeCardTemporary _ -> { }
                case null -> { }
                default -> System.out.println("ERROR: Wrong parent class type of upgrade shop card: " + cardField.getCard().getDisplayName() + ". Check card and GameView.setUpgradeShop().");
            }
            cardFieldView.setStyle(
                    "-fx-background-color: transparent; "
            );

            if (!localPlayersTurn && playerUpgrading != null) {
                otherPlayerTurnText.setText(gameController.getPlayerUpgrading().getName() + " is buying upgrades.");
            }
            otherPlayerTurnText.setVisible(!localPlayersTurn);

            cardFieldView.setDisable(!localPlayersTurn);
            upgradeShopCardsHBox.setDisable(!localPlayersTurn);
            cardFieldView.getCardImageView().setDisable(!localPlayersTurn);
            cardFieldView.getCardForegroundImageView().setDisable(!localPlayersTurn);

            finishUpgradingButton.setVisible(localPlayersTurn && !gameController.getIsLocalPlayerReadyForNextPhase());
            finishUpgradingButton.setDisable(!localPlayersTurn || gameController.getIsLocalPlayerReadyForNextPhase() || !gameController.getUnresolvedLocalChoices().isEmpty());
            finishUpgradingButton.setMouseTransparent(!localPlayersTurn);

            GridPane.setHalignment(cardFieldView, HPos.CENTER);
            GridPane.setMargin(cardFieldView, new Insets(0, 2, 0, 2));

            // Hovering the card
            cardFieldView.setOnMouseEntered(_ -> {
                cardShowcaseImageView.setImage(cardFieldView.getCardImageView().getImage());
                cardShowcaseImageView.setVisible(true);
            });
            cardFieldView.setOnMouseExited(_ -> cardShowcaseImageView.setVisible(false));
        }
    }

    private @NotNull Text getOtherPlayerTurnText(Font textFont) {
        Text otherPlayerTurnText = new Text();
        otherPlayerTurnText.setFont(textFont);
        otherPlayerTurnText.setFill(Color.WHITE);
        otherPlayerTurnText.setTextAlignment(TextAlignment.CENTER);
        return otherPlayerTurnText;
    }

    /**
     * Gets the SpaceViews from a mouse event, whose bounds surround the mouse.
     * @param event The mouse event to check for SpaceViews under the mouse.
     * @return Returns a new list of SpaceViews.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private List<SpaceView> getSpaceViewsAtMouse(MouseEvent event) {
        return getSpaceViewsAtPosition(new Point2D(event.getSceneX(), event.getSceneY()));
    }

    /**
     * The method for getting all the SpaceViews on the GameView, where the mouse position is within.
     * @param position The mouse position in the scene.
     * @return Returns a new list of SpaceViews at the mouse position.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private List<SpaceView> getSpaceViewsAtPosition(Point2D position) {
        List<SpaceView> spacesAtMouse = new ArrayList<>();
        for (SpaceView[] spaceViewColumn : spaceViews) {
            for (SpaceView spaceView : spaceViewColumn) {
                if (spaceView == null) continue;
                Bounds localBounds = spaceView.getBoundsInLocal();
                Bounds sceneBounds = spaceView.localToScene(localBounds);
                if (sceneBounds.contains(position)) {
                    // If mouse is within bounds of a node
                    spacesAtMouse.add(spaceView);
                }
            }
        }
        return spacesAtMouse;
    }

    /**
     * Method for showing the directionOptionsPane at the position of a SpaceView.
     * @param spaceView The SpaceView at which to show the directionOptionsPane.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setDirectionOptionsPane(SpaceView spaceView) {
        directionOptionsPane.setDisable(false);
        directionOptionsPane.setVisible(true);
        directionOptionsPane.setLayoutX(spaceView.getLayoutX() - (directionOptionsPane.getWidth() / 3));
        directionOptionsPane.setLayoutY(spaceView.getLayoutY() - (directionOptionsPane.getHeight() / 3));
    }

    /**
     * Updates the board element imageview to show the new SpawnPoint image with the color of the player.
     * @param space The space at which the player set its SpawnPoint.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void initializePlayerSpawnSpaceView(Space space) {
        spaceViews[space.x][space.y].updateBoardElementImage();
    }

    /**
     * Override of method from ViewObserver.
     * @param subject The subject that called the method.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            //statusLabel.setText(board.getStatusMessage());

            Space directionOptionsSpace = gameController.getDirectionOptionsSpace();
            if (directionOptionsSpace != null) {
                setDirectionOptionsPane(spaceViews[directionOptionsSpace.x][directionOptionsSpace.y]);
            } else {
                directionOptionsPane.setDisable(true);
                directionOptionsPane.setVisible(false);
            }

            if (board.getCurrentPhase().equals(GamePhase.INITIALIZATION)) {
                for (Player player : board.getPlayers()) {
                    Space playerSpace = player.getSpace();
                    if (playerSpace != null) {
                        initializePlayerSpawnSpaceView(playerSpace);
                    }
                }
            }

            boolean isPhaseWithReadyCheck = board.getCurrentPhase().equals(GamePhase.PROGRAMMING) || board.getCurrentPhase().equals(GamePhase.INITIALIZATION);
            for (Player player : board.getPlayers()) {
                Space playerSpace = player.getSpace();
                if (playerSpace != null) {
                    boolean playerIsReady = gameController.getIsPlayerReadyForNextPhase(player);
                    spaceViews[playerSpace.x][playerSpace.y].setReadyTickVisible(playerIsReady && isPhaseWithReadyCheck);
                }
            }

            Platform.runLater(() -> {
                if (board.getCurrentPhase().equals(GamePhase.UPGRADE) && !gameController.isHandlingPrePhase()) {
                    upgradeShopParentPane.setVisible(true);
                    upgradeShopParentPane.setMouseTransparent(false);
                    finishUpgradingButton.setVisible(gameController.getPlayerUpgrading() != null && !gameController.getIsLocalPlayerReadyForNextPhase());
                    updateUpgradeShop();
                } else {
                    if (upgradeShopParentPane != null) {
                        upgradeShopParentPane.setVisible(false);
                        upgradeShopParentPane.setMouseTransparent(true);
                    }
                }
            });
        }
    }

    /**
     * Hides the directionOptionsPane.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void handleDirectionButtonClicked() {
        directionOptionsPane.setDisable(true);
        directionOptionsPane.setVisible(false);
    }

    /**
     * Class for handling user input on SpaceViews.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private class SpaceEventHandler implements EventHandler<MouseEvent> {
        final public GameController gameController;

        public SpaceEventHandler(@NotNull GameController gameController) {
            this.gameController = gameController;
        }

        @Override
        public void handle(MouseEvent event) {
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                // Object source = event.getSource();
                List<SpaceView> spaceViews = getSpaceViewsAtMouse(event);
                if (!spaceViews.isEmpty()) {
                    SpaceView spaceView = spaceViews.getFirst();
                    //SpaceView spaceView = (SpaceView) source;
                    Space space = spaceView.space;
                    Board board = space.board;

                    if (board == gameController.board) {
                        if (event.isAltDown()) {
                            // Debugging
                            /*if (DEBUG_ALLOW_MANUAL_PLAYER_POSITION) {
                                if (board.getCurrentPhase() != GamePhase.INITIALIZATION && space.getPlayer() == null) {
                                    // Move the player to the hovered free space.
                                    playerView.getPlayer().setSpace(space);
                                }
                            }*/
                        } else {
                            // Game input
                            gameController.spacePressed(space);
                        }

                        event.consume();
                    }
                }
            }
        }
    }
}
