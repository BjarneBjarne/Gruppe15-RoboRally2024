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
package com.group15.roborally.client.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.group15.observer.Observer;
import com.group15.observer.Subject;
import com.group15.roborally.client.view.InfoPaneView;
import com.group15.roborally.client.view.MultiplayerMenuView;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.RoboRally;
import com.group15.roborally.client.coursecreator.CC_CourseData;
import com.group15.roborally.client.coursecreator.CC_JsonUtil;
import com.group15.roborally.client.exceptions.EmptyCourseException;
import com.group15.roborally.client.exceptions.GameLoadingException;
import com.group15.roborally.client.exceptions.NoCoursesException;
import com.group15.roborally.client.utils.Adapter;
import com.group15.roborally.client.utils.SaveAndLoadUtils;
import com.group15.roborally.client.templates.BoardTemplate;
import com.group15.roborally.client.model.boardelements.BoardElement;

import com.group15.roborally.server.model.Player;
import com.group15.roborally.server.model.GamePhase;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.util.Pair;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.io.File;

import static com.group15.roborally.client.BoardOptions.*;

/**
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class AppController implements Observer {
    private static RoboRally roboRally;
    public boolean isCourseCreatorOpen = false;
    @Setter
    private static GameController gameController;
    @Getter
    private static List<CC_CourseData> courses = new ArrayList<>();

    private static final NetworkingController networkingController = new NetworkingController();

    private MultiplayerMenuView multiplayerMenuView;
    private static InfoPaneView infoPane;

    public AppController(@NotNull RoboRally roboRally, InfoPaneView infoPane) {
        AppController.roboRally = roboRally;
        AppController.infoPane = infoPane;
        networkingController.attach(this);
    }

    public static void setInfoText(String text) {
        infoPane.setInfoText(text);
    }

    /**
     * Method for going to the join/host multiplayer menu.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void initializeMultiplayerMenu() {
        infoPane.setInfoText("Setting up multiplayer...");
        Platform.runLater(() -> {
            multiplayerMenuView = new MultiplayerMenuView();
            multiplayerMenuView.setControllers(networkingController);
            roboRally.createMultiplayerMenu(multiplayerMenuView);
            multiplayerMenuView.setupMenuUI();
            multiplayerMenuView.setupBackButton(roboRally::goToMainMenu);
            infoPane.setInfoText("");
            multiplayerMenuView.setServerURLInput("http://localhost:8080");
        });
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public static void startGame(CC_CourseData courseData, HashMap<Long, Player> players, long localPlayerId) {
        Pair<List<Space[][]>, Space[][]> courseSpaces = courseData.getGameSubBoards();
        Board board = new Board(courseSpaces.getKey(), courseSpaces.getValue(), courseData.getCourseName(), courseData.getNoOfCheckpoints());

        com.group15.roborally.client.model.Player localClient = null;

        // Adding players
        for (Player player : players.values()) {
            com.group15.roborally.client.model.Player newClient = new com.group15.roborally.client.model.Player(player.getPlayerId(),
                    player.getPlayerName(),
                    board,
                    Objects.requireNonNull(Robots.getRobotByName(player.getRobotName())));

            if (player.getPlayerId() == localPlayerId) {
                localClient = newClient;
            }

            newClient.setHeading(Heading.EAST);
            board.addPlayer(newClient);
        }

        // GameController
        gameController = new GameController(board, localClient, networkingController);
        //board.setCurrentPlayer(board.getPlayer(0));
        roboRally.createBoardView(gameController);
    }

    /**
     * Load a game from a file. A new game controller is created, with the board loaded
     * from the file. The GamePhase of the game is manually set to Programming.
     * @param loadedFile the .json to be deserialized into a board
     *
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void loadGame(File loadedFile) throws GameLoadingException {
        // Loading file
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(BoardElement.class, new Adapter<BoardElement>()).
                excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
        Gson gson = simpleBuilder.create();
        BoardTemplate boardTemplate = null;
        try {
            Scanner scanner = new Scanner(loadedFile);
            StringBuilder boardStringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                boardStringBuilder.append(scanner.nextLine());
            }
            scanner.close();
            boardTemplate = gson.fromJson(boardStringBuilder.toString(), BoardTemplate.class);
        } catch (IOException e1) {
            System.out.println(e1.getMessage());
        }
        if (boardTemplate == null) {
            throw new GameLoadingException();
        }

        // Creating and initializing the GameController, Board, and Players.
        // Board
        Board newBoard;
        try {
            newBoard = SaveAndLoadUtils.loadBoard(boardTemplate, courses);
        } catch (EmptyCourseException e) {
            System.out.println(e.getMessage());
            return;
        }

        // GameController
        gameController = new GameController(newBoard, null, networkingController);
        SaveAndLoadUtils.loadPlayers(boardTemplate, newBoard, gameController);
        //newBoard.setCurrentPhase(GamePhase.PROGRAMMING);

        // Players
        for (int i = 0; i < newBoard.getNoOfPlayers(); i++) {
            com.group15.roborally.client.model.Player player = newBoard.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < com.group15.roborally.client.model.Player.NO_OF_REGISTERS; j++) {
                    CardField field = player.getProgramField(j);
                    field.setVisible(true);
                }
                for (int j = 0; j < NO_OF_CARDS_IN_HAND; j++) {
                    CardField field = player.getCardField(j);
                    field.setVisible(true);
                }
            }
        }
        newBoard.setCurrentPlayer(newBoard.getPlayer(0));

        roboRally.createBoardView(gameController);
    }


    /**
     * Save the current game to a file with the given file name. If the
     * game is not in the programming phase, the game is not saved and
     * the method returns false. Otherwise, the game is saved and the
     * method returns true.
     *
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @return true if the game was saved, false otherwise
     */
    public boolean saveGame(File file) {
        if (gameController.board.getCurrentPhase() != GamePhase.PROGRAMMING) {
            return false;
        }
        SaveAndLoadUtils.saveBoard(gameController.board, file);
        return true;
    }

    /**
     * sets ends game
     * @author Maximillian Bjørn Mortensen
     */
    public static void gameOver(com.group15.roborally.client.model.Player winner) {
        roboRally.goToWinScreen(gameController, winner);
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
            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void quit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isEmpty() || result.get() != ButtonType.OK) {
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

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void createCourseCreator(Scene primaryScene) {
        roboRally.createCourseCreator(primaryScene);
        isCourseCreatorOpen = true;
    }

    /**
     * Loads the courses from resources and puts them in the courses variable.
     * @throws NoCoursesException If there were not found any courses.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void loadCourses() throws NoCoursesException {
        // Loading courses
        courses = CC_JsonUtil.getCoursesInFolder("courses");
        if (courses.isEmpty()) {
            throw new NoCoursesException();
        }
    }

    public void resetMultiplayerMenuView() {
        multiplayerMenuView = null;
    }

    public void disconnectFromServer(String s, int i) {
        networkingController.disconnectFromServer(s, i);
    }

    @Override
    public void update(Subject subject) {
        if (subject.equals(networkingController)) {
            if (!networkingController.isConnectedToGame) {
                roboRally.goToMainMenu();
            }
        }
    }
}
