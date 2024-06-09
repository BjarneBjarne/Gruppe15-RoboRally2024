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
package gruppe15.roborally.view;

import gruppe15.observer.Subject;
import gruppe15.roborally.ApplicationSettings;
import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.*;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardPermanent;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardTemporary;
import gruppe15.utils.ImageUtils;
import gruppe15.utils.TextUtils;
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
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.jetbrains.annotations.NotNull;

import static gruppe15.roborally.ApplicationSettings.*;
import static gruppe15.roborally.model.Phase.INITIALIZATION;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for making the main JavaFX nodes for gameplay.
 * This includes the view of the board, along with the views for each of its spaces, as well as the view containing -
 *     the player mats.
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class BoardView extends VBox implements ViewObserver {
    private final GameController gameController;
    private final Board board;

    private final StackPane mainBoardPane;
    private final SpaceView[][] spaceViews;
    private final PlayersView playersView;
    private final GridPane directionOptionsPane;
    private StackPane upgradeShopPane;
    private HBox upgradeShopCardsHBox;
    //private final Label statusLabel;

    /**
     * Constructor of BoardView.
     * @param gameController
     * @param directionOptionsPane The loaded directionOptionsPane from the DirectionArrows.fxml file.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public BoardView(@NotNull GameController gameController, GridPane directionOptionsPane) {
        this.gameController = gameController;
        this.directionOptionsPane = directionOptionsPane;
        board = gameController.board;
        board.initializeUpgradeShop();
        spaceViews = new SpaceView[board.width][board.height];
        this.directionOptionsPane.setPrefSize(ApplicationSettings.SPACE_SIZE * 3, SPACE_SIZE * 3);

        List<Node> children = this.directionOptionsPane.getChildren();
        for (Node child : children) {
            if (child instanceof Button button) {
                gameController.initializeDirectionButton(button, this);
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

        GridPane boardTilesPane = new GridPane();
        boardTilesPane.setAlignment(Pos.CENTER);
        AnchorPane anchorPane = new AnchorPane(directionOptionsPane);
        StackPane interactablePane = new StackPane(boardTilesPane, anchorPane);
        StackPane.setAlignment(boardTilesPane, Pos.CENTER);
        StackPane.setAlignment(anchorPane, Pos.CENTER);
        ZoomableScrollPane zoomableScrollPane = new ZoomableScrollPane(interactablePane, 0.9);
        zoomableScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        zoomableScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainBoardPane = new StackPane(zoomableScrollPane);
        mainBoardPane.setAlignment(Pos.CENTER);
        interactablePane.getStyleClass().add("transparent-scroll-pane");
        zoomableScrollPane.getStyleClass().add("transparent-scroll-pane");
        mainBoardPane.getStyleClass().add("transparent-scroll-pane");
        interactablePane.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0), CornerRadii.EMPTY, null)));
        zoomableScrollPane.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0), CornerRadii.EMPTY, null)));
        mainBoardPane.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0), CornerRadii.EMPTY, null)));
        interactablePane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        zoomableScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        mainBoardPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        StackPane.setMargin(zoomableScrollPane, new Insets(25, 0, 0, 0));
        double width = boardTilesPane.getWidth() + 12000;
        double height = width / 2.72;
        interactablePane.setMinSize(width, height);
        interactablePane.setPrefSize(width, height);
        interactablePane.setMaxSize(width, height);
        mainBoardPane.setPrefHeight(895);
        zoomableScrollPane.setPannable(true);
        //statusLabel = new Label("<no status>");

        playersView = new PlayersView(gameController);
        StackPane playersViewStackPane = new StackPane(playersView);
        playersViewStackPane.setAlignment(Pos.CENTER);
        playersViewStackPane.setPrefHeight(464);
        //this.getChildren().add(statusLabel);
        this.getChildren().add(mainBoardPane);
        this.getChildren().add(playersViewStackPane);
        this.setAlignment(Pos.BOTTOM_CENTER);
        this.setFillWidth(true);

        VBox.setVgrow(mainBoardPane, Priority.ALWAYS);
        VBox.setVgrow(playersViewStackPane, Priority.ALWAYS);

        mainBoardPane.setMinWidth(APP_BOUNDS.getWidth());
        mainBoardPane.setPrefWidth(APP_BOUNDS.getWidth());

        playersView.setMinWidth(APP_BOUNDS.getWidth());
        playersView.setPrefWidth(APP_BOUNDS.getWidth());

        //StackPane.setMargin(playersView, new Insets(0, 0, 0, 0));

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                if (space == null) continue;
                SpaceView spaceView = new SpaceView(space);
                spaceViews[x][y] = spaceView;
                boardTilesPane.add(spaceView, x, y);
            }
        }

        SpaceEventHandler spaceEventHandler = new SpaceEventHandler(gameController);
        mainBoardPane.setOnMouseClicked(spaceEventHandler);

        board.attach(this);
        update(board);
    }

    /**
     * Method for initializing the upgrade shop. Should only be called once after creating a BoardView.
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

        mainBoardPane.getChildren().add(upgradeShopPane);

        if (this.upgradeShopCardsHBox == null) {
            System.out.println("upgradeShopCardsHBox not initialized in BoardView - setUpgradeShopFXML()");
        }
        if (finishUpgradingButton == null) {
            System.out.println("finishUpgradingButton not initialized in BoardView - setUpgradeShopFXML()");
        }

        finishUpgradingButton.setOnMouseClicked(event -> {
            gameController.startProgrammingPhase();
            upgradeShopPane.setVisible(false);
            upgradeShopPane.setMouseTransparent(true);
        });

        // Button text
        Font textFont = TextUtils.loadFont("OCRAEXT.TTF", 42);
        Text buttonText = new Text();
        buttonText.setFont(textFont);
        buttonText.setFill(Color.WHITE);
        buttonText.setStroke(Color.BLACK);
        buttonText.setStrokeWidth(2);
        buttonText.setStrokeType(StrokeType.OUTSIDE);
        buttonText.setText("Finish Upgrading");
        buttonText.setTextAlignment(TextAlignment.CENTER);

        finishUpgradingButton.setGraphic(buttonText);

        upgradeShopTitelPane.setStyle(
                "-fx-background-color: rgba(0,0,0,.5); " +
                "-fx-background-radius: 15px"
        );
        upgradeShopMainPane.setStyle(
                "-fx-background-color: rgba(0,0,0,.5); " +
                        "-fx-background-radius: 15px"
        );
    }

    /**
     * Method for refreshing the card views, to show the new cards that are available for purchase.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void updateUpgradeShop() {
        upgradeShopCardsHBox.getChildren().clear();
        UpgradeShop upgradeShop = board.getUpgradeShop();
        CardFieldView[] upgradeShopCardViews = new CardFieldView[board.getNoOfPlayers()];

        for (int i = 0; i < board.getNoOfPlayers(); i++) {
            CardField cardField = upgradeShop.getAvailableCardsField(i);
            CardFieldView cardFieldView = new CardFieldView(gameController, cardField, 1 * 1.5, 1.6 * 1.5);
            upgradeShopCardViews[i] = cardFieldView;
            upgradeShopCardsHBox.getChildren().add(cardFieldView);
            cardFieldView.setAlignment(Pos.CENTER);
            //String boarderColorString = "-fx-border-color: black; ";
            if (cardField.getCard() instanceof UpgradeCardPermanent) {
                //boarderColorString = "-fx-border-color: #dfcb45; ";
            } else if (cardField.getCard() instanceof UpgradeCardTemporary) {
                //boarderColorString = "-fx-border-color: #a62a24; ";
            } else if (cardField.getCard() == null) {
                //boarderColorString = "-fx-border-color: transparent; ";
            } else {
                System.out.println("ERROR: Wrong parent class type of upgrade shop card: " + cardField.getCard().getName() + ". Check card and BoardView.setUpgradeShop().");
            }
            cardFieldView.setStyle(
                    "-fx-background-color: transparent; " +
                            //boarderColorString +
                            "-fx-border-width: 2px 2px 2px 2px;" +
                            "-fx-border-radius: 5"
            );
            //cardFieldView.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-border-width: 2px;");
            GridPane.setHalignment(cardFieldView, HPos.CENTER);
            GridPane.setMargin(cardFieldView, new Insets(0, 2, 0, 2));
        }
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
     * The method for getting all the SpaceViews on the BoardView, where the mouse position is within.
     * @param position The mouse position in the scene.
     * @return Returns a new list of SpaceViews at the mouse position.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private List<SpaceView> getSpaceViewsAtPosition(Point2D position) {
        List<SpaceView> spacesAtMouse = new ArrayList<>();
        for (int x = 0; x < spaceViews.length; x++) {
            for (int y = 0; y < spaceViews[x].length; y++) {
                SpaceView space = spaceViews[x][y];
                if (space == null) continue;
                Bounds localBounds = space.getBoundsInLocal();
                Bounds sceneBounds = space.localToScene(localBounds);
                if (sceneBounds.contains(position)) {
                    // If mouse is within bounds of a node
                    spacesAtMouse.add(space);
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
            }

            Platform.runLater(() -> {
                if (board.getCurrentPhase() == Phase.UPGRADE) {
                    updateUpgradeShop();
                    upgradeShopPane.setVisible(true);
                    upgradeShopPane.setMouseTransparent(false);
                } else {
                    if (upgradeShopPane != null) {
                        upgradeShopPane.setVisible(false);
                        upgradeShopPane.setMouseTransparent(true);
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
                            if (DEBUG_ALLOW_MANUAL_PLAYER_POSITION) {
                                if (board.getCurrentPhase() != INITIALIZATION && space.getPlayer() == null) {
                                    // Move the player to the hovered free space.
                                    playersView.getSelectedPlayerView().getPlayer().setSpace(space);
                                }
                            }
                        } else {
                            // Game input
                            gameController.spacePressed(event, space);
                        }

                        event.consume();
                    }
                }
            }
        }
    }
}
