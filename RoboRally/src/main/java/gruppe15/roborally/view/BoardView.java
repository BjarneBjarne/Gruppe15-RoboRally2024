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
import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.*;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardPermanent;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardTemporary;
import gruppe15.roborally.model.utils.Constants;
import gruppe15.roborally.model.utils.ImageUtils;
import gruppe15.roborally.model.utils.TextUtils;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

import static gruppe15.roborally.model.utils.Constants.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class BoardView extends VBox implements ViewObserver {

    private Board board;

    private StackPane mainBoardPane;

    private GridPane boardTilesPane;
    private SpaceView[][] spaceViews;

    private PlayersView playersView;
    private Label statusLabel;

    private StackPane upgradeShopPane;
    private StackPane upgradeShopTitelPane;
    private StackPane upgradeShopMainPane;
    private HBox upgradeShopCardsHBox;
    private Button finishUpgradingButton;
    private CardFieldView[] upgradeShopCardViews;

    private final SpaceEventHandler spaceEventHandler;
    private final GameController gameController;
    private final GridPane directionOptionsPane;

    private ZoomableScrollPane zoomableScrollPane;
    private StackPane interactablePane = new StackPane();

    public BoardView(@NotNull GameController gameController, GridPane directionOptionsPane) {
        this.gameController = gameController;
        this.directionOptionsPane = directionOptionsPane;
        board = gameController.board;
        board.initializeUpgradeShop();
        spaceViews = new SpaceView[board.width][board.height];
        spaceEventHandler = new SpaceEventHandler(gameController);
        this.directionOptionsPane.setPrefSize(Constants.SPACE_SIZE * 3, SPACE_SIZE * 3);

        List<Node> children = this.directionOptionsPane.getChildren();
        for (Node child : children) {
            if (child instanceof Button button) {
                gameController.initializeDirectionButton(button, this);
                ImageView buttonImage = new ImageView();
                buttonImage.setFitWidth(Constants.SPACE_SIZE);
                buttonImage.setFitHeight(SPACE_SIZE);
                Heading direction = Heading.valueOf(button.getId());
                buttonImage.setImage(ImageUtils.getRotatedImageByHeading(ImageUtils.getImageFromName("arrow.png"), direction));
                button.setGraphic(buttonImage);
            }
        }
        this.directionOptionsPane.setDisable(true);
        this.directionOptionsPane.setVisible(false);

        boardTilesPane = new GridPane();
        boardTilesPane.setAlignment(Pos.CENTER);
        playersView = new PlayersView(gameController);
        StackPane playersViewStackPane = new StackPane(playersView);

        AnchorPane anchorPane = new AnchorPane(directionOptionsPane);
        interactablePane = new StackPane(boardTilesPane, anchorPane);
        StackPane.setAlignment(boardTilesPane, Pos.CENTER);
        StackPane.setAlignment(anchorPane, Pos.CENTER);
        zoomableScrollPane = new ZoomableScrollPane(interactablePane);
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
        Platform.runLater(() -> {
            interactablePane.setMinSize(boardTilesPane.getWidth() + 2000, boardTilesPane.getHeight() + 750);
            interactablePane.setPrefSize(boardTilesPane.getWidth() + 2000, boardTilesPane.getHeight() + 750);
            interactablePane.setMaxSize(boardTilesPane.getWidth() + 2000, boardTilesPane.getHeight() + 750);
            mainBoardPane.setPrefHeight(895);
            zoomableScrollPane.setPannable(true);
            centerBoard();
        });

        statusLabel = new Label("<no status>");

        playersViewStackPane.setAlignment(Pos.CENTER);
        this.getChildren().add(statusLabel);
        this.getChildren().add(mainBoardPane);
        this.getChildren().add(playersViewStackPane);
        this.setAlignment(Pos.BOTTOM_CENTER);
        this.setFillWidth(true);

        VBox.setVgrow(mainBoardPane, Priority.ALWAYS);
        VBox.setVgrow(playersViewStackPane, Priority.ALWAYS);

        mainBoardPane.setPrefWidth(APP_BOUNDS.getWidth());
        mainBoardPane.setMinWidth(APP_BOUNDS.getWidth());

        playersView.setPrefWidth(APP_BOUNDS.getWidth());
        playersView.setMinWidth(APP_BOUNDS.getWidth());

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                if (space == null) continue;
                SpaceView spaceView = new SpaceView(space);
                spaceViews[x][y] = spaceView;
                boardTilesPane.add(spaceView, x, y);
            }
        }

        mainBoardPane.setOnMouseClicked(spaceEventHandler);

        board.attach(this);
        update(board);
    }

    /**
     * Simple constructor used when loading a game.
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param gameController the game controller
     */
    public BoardView(@NotNull GameController gameController) {
        this.gameController = gameController;
        board = gameController.board;
        spaceViews = new SpaceView[board.width][board.height];
        spaceEventHandler = new SpaceEventHandler(gameController);
        this.directionOptionsPane = new GridPane();

        boardTilesPane = new GridPane();
        playersView = new PlayersView(gameController);
        statusLabel = new Label("<no status>");
        // AnchorPane anchorPane = new AnchorPane(directionOptionsPane);
        mainBoardPane = new StackPane(boardTilesPane);
        this.getChildren().add(statusLabel);
        this.getChildren().add(mainBoardPane);
        this.getChildren().add(playersView);
        this.setAlignment(Pos.TOP_CENTER);
        boardTilesPane.setAlignment(Pos.TOP_CENTER);
        mainBoardPane.setAlignment(Pos.TOP_CENTER);

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                SpaceView spaceView = new SpaceView(space);
                spaceViews[x][y] = spaceView;
                boardTilesPane.add(spaceView, x, y);
            }
        }

        mainBoardPane.setOnMouseClicked(spaceEventHandler);

        board.attach(this);
        update(board);
    }

    public void setUpgradeShopFXML(StackPane upgradeShopPane, StackPane upgradeShopTitelPane, StackPane upgradeShopMainPane, HBox upgradeShopCardsHBox, Button finishUpgradingButton) {
        this.upgradeShopPane = upgradeShopPane;
        this.upgradeShopTitelPane = upgradeShopTitelPane;
        this.upgradeShopMainPane = upgradeShopMainPane;
        this.upgradeShopCardsHBox = upgradeShopCardsHBox;
        this.finishUpgradingButton = finishUpgradingButton;

        mainBoardPane.getChildren().add(upgradeShopPane);

        if (this.upgradeShopCardsHBox == null) {
            System.out.println("upgradeShopCardsHBox not initialized in BoardView - setUpgradeShopFXML()");
        }
        if (this.finishUpgradingButton == null) {
            System.out.println("finishUpgradingButton not initialized in BoardView - setUpgradeShopFXML()");
        }

        finishUpgradingButton.setOnMouseClicked(event -> {
            gameController.startProgrammingPhase();
            upgradeShopPane.setVisible(false);
            upgradeShopPane.setMouseTransparent(true);
        });

        // Button text
        Font textFont = TextUtils.loadFont("OCRAEXT.TTF", 36);
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
                "-fx-background-color: rgba(0,0,0,.35); " +
                "-fx-background-radius: 15px"
        );
        upgradeShopMainPane.setStyle(
                "-fx-background-color: rgba(0,0,0,.35); " +
                        "-fx-background-radius: 15px"
        );
    }
    
    public void setUpgradeShop() {
        upgradeShopCardsHBox.getChildren().clear();
        UpgradeShop upgradeShop = board.getUpgradeShop();
        upgradeShopCardViews = new CardFieldView[board.getNoOfPlayers()];
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

    private List<SpaceView> getSpaceViewsAtMouse(MouseEvent event) {
        return getSpaceViewsAtPosition(new Point2D(event.getSceneX(), event.getSceneY()));
    }

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

    public void setDirectionOptionsPane(SpaceView spaceView) {
        directionOptionsPane.setDisable(false);
        directionOptionsPane.setVisible(true);
        directionOptionsPane.setLayoutX(spaceView.getLayoutX() - (directionOptionsPane.getWidth() / 3));
        directionOptionsPane.setLayoutY(spaceView.getLayoutY() - (directionOptionsPane.getHeight() / 3));
    }

    public void initializePlayerSpawnSpaceView(Space space) {
        spaceViews[space.x][space.y].updateBoardElementImage();
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            statusLabel.setText(board.getStatusMessage());

            Space directionOptionsSpace = gameController.getDirectionOptionsSpace();
            if (directionOptionsSpace != null) {
                setDirectionOptionsPane(spaceViews[directionOptionsSpace.x][directionOptionsSpace.y]);
            }

            Platform.runLater(() -> {
                if (board.getPhase() == Phase.UPGRADE) {
                    setUpgradeShop();
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

    public void handleDirectionButtonClicked() {
        directionOptionsPane.setDisable(true);
        directionOptionsPane.setVisible(false);
    }

    public void centerBoard() {
        Platform.runLater(() -> {
            zoomableScrollPane.setVvalue(0.5);
            zoomableScrollPane.setHvalue(0.5);
        });
    }


    // XXX this handler and its uses should eventually be deleted! This is just to help test the
    //     behaviour of the game by being able to explicitly move the players on the board!
    private class SpaceEventHandler implements EventHandler<MouseEvent> {
        final public GameController gameController;
        private int playerToMove = 0;
        private SpaceView selectedSpaceView;
        private Player playerToSpawn;

        public SpaceEventHandler(@NotNull GameController gameController) {
            this.gameController = gameController;
        }

        @Override
        public void handle(MouseEvent event) {
            // Object source = event.getSource();
            List<SpaceView> spaceViews = getSpaceViewsAtMouse(event);
            if (!spaceViews.isEmpty()) {
                SpaceView spaceView = spaceViews.getFirst();
                //SpaceView spaceView = (SpaceView) source;
                Space space = spaceView.space;
                Board board = space.board;

                if (board == gameController.board) {
                    gameController.spacePressed(event, space);
                    event.consume();
                }
            }
        }
    }
}
