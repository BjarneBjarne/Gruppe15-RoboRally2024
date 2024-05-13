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
import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.boardelements.BE_SpawnPoint;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

import static gruppe15.roborally.model.Phase.INITIALISATION;
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
    private SpaceView[][] spaces;

    private PlayersView playersView;

    private Label statusLabel;

    private SpaceEventHandler spaceEventHandler;

    private final GridPane directionOptionsPane;

    public BoardView(@NotNull GameController gameController, GridPane directionOptionsPane) {
        board = gameController.board;
        spaces = new SpaceView[board.width][board.height];
        spaceEventHandler = new SpaceEventHandler(gameController);
        this.directionOptionsPane = directionOptionsPane;
        this.directionOptionsPane.setPrefSize(SPACE_WIDTH * 3, SPACE_HEIGHT * 3);

        List<Node> children = this.directionOptionsPane.getChildren();
        for (Node child : children) {
            if (child instanceof Button button) {
                ImageView buttonImage = new ImageView();
                buttonImage.setFitWidth(SPACE_WIDTH);
                buttonImage.setFitHeight(SPACE_HEIGHT);
                Heading direction = Heading.valueOf(button.getId());
                buttonImage.setImage(ImageUtils.getRotatedImageByHeading(ImageUtils.getImageFromName("arrow.png"), direction));
                button.setGraphic(buttonImage);

                button.setOnMouseClicked(event -> {
                    gameController.chooseDirection(direction);
                    directionOptionsPane.setDisable(true);
                    directionOptionsPane.setVisible(false);
                });
            }
        }
        this.directionOptionsPane.setDisable(true);
        this.directionOptionsPane.setVisible(false);

        boardTilesPane = new GridPane();
        playersView = new PlayersView(gameController);
        statusLabel = new Label("<no status>");
        AnchorPane anchorPane = new AnchorPane(directionOptionsPane);
        mainBoardPane = new StackPane(boardTilesPane, anchorPane);
        this.getChildren().add(mainBoardPane);
        this.getChildren().add(playersView);
        this.getChildren().add(statusLabel);
        this.setAlignment(Pos.CENTER);
        boardTilesPane.setAlignment(Pos.CENTER);
        mainBoardPane.setAlignment(Pos.CENTER);

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                // DialogPane dialog = new DialogPane();
                //     ImageView imageIn = new ImageView(board.getSpace(0, 3).getImage());
                //     HBox images = new HBox();
                //     images.setSpacing(10);
                //     images.getChildren().addAll(imageIn);
                //     dialog.setContent(images);
                //     Alert alert = new Alert(Alert.AlertType.INFORMATION);
                //     alert.setHeaderText("Images");
                //     alert.setDialogPane(dialog);
                //     alert.setTitle("Maybe");
                //     alert.showAndWait();
                Space space = board.getSpace(x, y);
                SpaceView spaceView = new SpaceView(space);
                spaces[x][y] = spaceView;
                boardTilesPane.add(spaceView, x, y);
            }
        }

        mainBoardPane.setOnMouseClicked(spaceEventHandler);
        mainBoardPane.setOnKeyPressed(event -> spaceEventHandler.keyPressed(event));

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
        board = gameController.board;
        spaces = new SpaceView[board.width][board.height];
        spaceEventHandler = new SpaceEventHandler(gameController);
        this.directionOptionsPane = new GridPane();
        // this.directionOptionsPane.setPrefSize(SPACE_WIDTH * 3, SPACE_HEIGHT * 3);

        // List<Node> children = this.directionOptionsPane.getChildren();
        // for (Node child : children) {
        //     if (child instanceof Button button) {
        //         ImageView buttonImage = new ImageView();
        //         buttonImage.setFitWidth(SPACE_WIDTH);
        //         buttonImage.setFitHeight(SPACE_HEIGHT);
        //         Heading direction = Heading.valueOf(button.getId());
        //         buttonImage.setImage(ImageUtils.getRotatedImageByHeading(ImageUtils.getImageFromName("arrow.png"), direction));
        //         button.setGraphic(buttonImage);

        //         button.setOnMouseClicked(event -> {
        //             gameController.chooseDirection(direction);
        //             directionOptionsPane.setDisable(true);
        //             directionOptionsPane.setVisible(false);
        //         });
        //     }
        // }
        // this.directionOptionsPane.setDisable(true);
        // this.directionOptionsPane.setVisible(false);

        boardTilesPane = new GridPane();
        playersView = new PlayersView(gameController);
        statusLabel = new Label("<no status>");
        // AnchorPane anchorPane = new AnchorPane(directionOptionsPane);
        mainBoardPane = new StackPane(boardTilesPane);
        this.getChildren().add(mainBoardPane);
        this.getChildren().add(playersView);
        this.getChildren().add(statusLabel);
        this.setAlignment(Pos.CENTER);
        boardTilesPane.setAlignment(Pos.CENTER);
        mainBoardPane.setAlignment(Pos.CENTER);

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                // DialogPane dialog = new DialogPane();
                //     ImageView imageIn = new ImageView(board.getSpace(0, 3).getImage());
                //     HBox images = new HBox();
                //     images.setSpacing(10);
                //     images.getChildren().addAll(imageIn);
                //     dialog.setContent(images);
                //     Alert alert = new Alert(Alert.AlertType.INFORMATION);
                //     alert.setHeaderText("Images");
                //     alert.setDialogPane(dialog);
                //     alert.setTitle("Maybe");
                //     alert.showAndWait();
                Space space = board.getSpace(x, y);
                SpaceView spaceView = new SpaceView(space);
                spaces[x][y] = spaceView;
                boardTilesPane.add(spaceView, x, y);
            }
        }

        mainBoardPane.setOnMouseClicked(spaceEventHandler);
        mainBoardPane.setOnKeyPressed(event -> spaceEventHandler.keyPressed(event));

        board.attach(this);
        update(board);
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            statusLabel.setText(board.getStatusMessage());
        }
    }

    private List<SpaceView> getSpacesAtMouse(MouseEvent event) {
        List<SpaceView> spacesAtMouse = new ArrayList<>();
        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                SpaceView space = spaces[x][y];
                Bounds localBounds = space.getBoundsInLocal();
                Bounds sceneBounds = space.localToScene(localBounds);
                if (sceneBounds.contains(new Point2D(event.getSceneX(), event.getSceneY()))) {
                    // If mouse is within bounds of a node
                    spacesAtMouse.add(space);
                }
            }
        }
        return spacesAtMouse;
    }



    // XXX this handler and its uses should eventually be deleted! This is just to help test the
    //     behaviour of the game by being able to explicitly move the players on the board!
    private class SpaceEventHandler implements EventHandler<MouseEvent> {
        final public GameController gameController;
        private int playerToMove = 0;

        public SpaceEventHandler(@NotNull GameController gameController) {
            this.gameController = gameController;
        }

        @Override
        public void handle(MouseEvent event) {
            directionOptionsPane.setDisable(true);
            directionOptionsPane.setVisible(false);
            // Object source = event.getSource();
            SpaceView spaceView = getSpacesAtMouse(event).getFirst();
            if (spaceView != null) {
                //SpaceView spaceView = (SpaceView) source;
                Space space = spaceView.space;
                Board board = space.board;

                if (board == gameController.board) {
                    if (space.getBoardElement() instanceof BE_SpawnPoint) {
                        Player currentPlayer = board.getCurrentPlayer();
                        if (board.getPhase() == INITIALISATION) {
                            if (space.getPlayer() == null) {
                                currentPlayer.setSpawn(space);
                                currentPlayer.setSpace(space);
                            }
                            directionOptionsPane.setDisable(false);
                            directionOptionsPane.setVisible(true);
                            directionOptionsPane.setLayoutX(spaceView.getLayoutX() - (directionOptionsPane.getPrefWidth() / 3));
                            directionOptionsPane.setLayoutY(spaceView.getLayoutY() - (directionOptionsPane.getPrefHeight() / 3));
                        }
                    } else {
                        if (event.isShiftDown()) {
                            space.setPlayer(board.getPlayer(1));
                        } else if (event.isControlDown()) {
                            space.setPlayer(board.getPlayer(0));
                        }
                    }
                    event.consume();
                }
            }
        }

        public void keyPressed(KeyEvent event) { // NOt working
            switch (event.getCode()) {
                case KeyCode.F1:
                    playerToMove = 0;
                    break;
                case KeyCode.F2:
                    playerToMove = 1;
                    break;
                case KeyCode.F3:
                    playerToMove = 2;
                    break;
                case KeyCode.F4:
                    playerToMove = 3;
                    break;
            }
        }
    }
}
