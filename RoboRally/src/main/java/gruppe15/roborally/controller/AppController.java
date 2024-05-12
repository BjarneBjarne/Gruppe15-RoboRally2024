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
package gruppe15.roborally.controller;

import gruppe15.observer.Observer;
import gruppe15.observer.Subject;
import gruppe15.roborally.RoboRally;
import gruppe15.roborally.fileaccess.LoadBoard;
import gruppe15.roborally.model.*;
import gruppe15.roborally.model.boardelements.BE_SpawnPoint;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.io.File;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {
    private final RoboRally roboRally;
    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    public void courseSelection() {
        roboRally.createSetupMenu(this);
    }

    public void beginCourse(int noOfPlayers, int mapIndex, String[] playerNames, String[] playerCharacters) {
        Board board = new Board(13,10, mapIndex);
        gameController = new GameController(board);

        // Finding spawns
        List<Space> spawnPoints = new ArrayList<>();
        Space[][] spaces = board.getSpaces();
        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                Space space = spaces[x][y];
                if (space.getBoardElement() instanceof BE_SpawnPoint) {
                    spawnPoints.add(space);
                }
            }
        }

        // Adding players
        for (int i = 0; i < noOfPlayers; i++) {
            Player player = new Player(board, Objects.requireNonNull(Robots.getRobotByName(playerCharacters[i])), playerNames[i]);
            player.setHeading(Heading.EAST);
            board.addPlayer(player);
        }

        board.setCurrentPlayer(board.getPlayer(0));
    }

    public void loadGame(File loadedFile) {
        Board newBoard = LoadBoard.loadBoard(loadedFile);
        System.out.println(newBoard.width);
        gameController = new GameController(newBoard);
        
        gameController.startProgrammingPhase();
        
        roboRally.createBoardView(gameController);
    }

    public void saveGame() {
        // XXX needs to be implemented eventually
    }

    public void loadGame() {
        // XXX needs to be implemented eventually
        // for now, we just create a new game
        if (gameController == null) {
            courseSelection();
        }
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
