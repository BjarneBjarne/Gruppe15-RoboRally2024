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
import gruppe15.roborally.coursecreator.CC_CourseData;
import gruppe15.roborally.fileaccess.LoadBoard;
import gruppe15.roborally.model.*;
import gruppe15.roborally.model.boardelements.BE_SpawnPoint;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.io.File;

import static gruppe15.roborally.GameSettings.*;

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

    public void beginCourse(CC_CourseData courseData, String[] playerNames, String[] playerCharacters) {
        Pair<List<Space[][]>, Space[][]> courseSpaces = courseData.getGameSubBoards();
        Board board = new Board(courseSpaces);

        gameController = new GameController(board, this);

        // Finding spawns
        List<Space> spawnPoints = new ArrayList<>();
        for (int x = 0; x < board.getSpaces().length; x++) {
            for (int y = 0; y < board.getSpaces()[x].length; y++) {
                Space space = board.getSpace(x, y);
                if (space != null && space.getBoardElement() instanceof BE_SpawnPoint) {
                    spawnPoints.add(space);
                }
            }
        }

        // Adding players
        for (int i = 0; i < NO_OF_PLAYERS; i++) {
            Player player = new Player(board, Objects.requireNonNull(Robots.getRobotByName(playerCharacters[i])), playerNames[i]);
            player.setHeading(Heading.EAST);
            board.addPlayer(player);
        }

        board.setCurrentPlayer(board.getPlayer(0));
        roboRally.createBoardView(gameController, false);
    }


    /**
     * Load a game from a file. A new game controller is created, with the board loaded
     * from the file. The Phase of the game is manually set to Programming.
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param loadedFile the .json to be deserialized into a board
     */
    public void loadGame(File loadedFile) {
        Board newBoard = LoadBoard.loadBoard(loadedFile);
        gameController = new GameController(newBoard, this);
        gameController.board.setPhase(Phase.PROGRAMMING);
        newBoard.setCurrentRegister(0);
        newBoard.updatePriorityList();
        newBoard.setCurrentPlayer(newBoard.getPriorityList().peek());

        for (int i = 0; i < newBoard.getNoOfPlayers(); i++) {
            Player player = newBoard.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_OF_REGISTERS; j++) {
                    CardField field = player.getProgramField(j);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_OF_CARDS; j++) {
                    CardField field = player.getCardField(j);
                    field.setVisible(true);
                }
            }
        }
        roboRally.createBoardView(gameController, true);
    }


    /**
     * Save the current game to a file with the given file name. If the
     * game is not in the programming phase, the game is not saved and
     * the method returns false. Otherwise, the game is saved and the
     * method returns true.
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param fileName the name of the file to which the game is saved
     * @return true if the game was saved, false otherwise
     */
    public boolean saveGame(File file) {
        if (gameController.board.getPhase() != Phase.PROGRAMMING) {
            return false;
        }
        LoadBoard.saveBoard(gameController.board, file);
        return true;
    }

    /**
     * sets ends game
     * @author Maximillian Bjørn Mortensen
     */
    public void gameOver(){
        roboRally.goToWinScreen(gameController, this);
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
            // saveGame(null);

            gameController = null;
            roboRally.createBoardView(null, false);
            return true;
        }
        return false;
    }

    /**
     * sets game controller to null
     * @param gameController
     * @author Maximillian Bjørn Mortensen
     */
    public void setGameController(GameController gameController){
        this.gameController = gameController;
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

    public void courseCreator(Scene primaryScene) {
        roboRally.createCourseCreator(primaryScene);
    }
}
