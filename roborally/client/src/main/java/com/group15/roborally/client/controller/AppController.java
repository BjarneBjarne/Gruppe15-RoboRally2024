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

import com.group15.roborally.client.utils.*;
import com.group15.roborally.common.observer.Observer;
import com.group15.roborally.common.observer.Subject;
import com.group15.roborally.client.model.networking.ServerDataManager;
import com.group15.roborally.client.view.InfoPaneView;
import com.group15.roborally.client.view.MultiplayerMenuView;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.RoboRally;
import com.group15.roborally.client.coursecreator.CC_CourseData;
import com.group15.roborally.client.coursecreator.CC_JsonUtil;
import com.group15.roborally.client.exceptions.EmptyCourseException;
import com.group15.roborally.client.exceptions.GameLoadingException;
import com.group15.roborally.client.exceptions.NoCoursesException;
import com.group15.roborally.client.templates.BoardTemplate;
import com.group15.roborally.client.model.boardelements.BoardElement;

import com.group15.roborally.common.model.Player;
import com.group15.roborally.common.model.GamePhase;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.util.Pair;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.io.File;

import static com.group15.roborally.client.LobbySettings.*;

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

    private final ServerDataManager serverDataManager;

    private static InfoPaneView infoPane;

    public AppController(@NotNull RoboRally roboRally, InfoPaneView infoPane, ServerDataManager serverDataManager) {
        AppController.roboRally = roboRally;
        AppController.infoPane = infoPane;
        this.serverDataManager = serverDataManager;
        this.serverDataManager.attach(this);
    }

    public static void setInfoText(String text) {
        infoPane.setInfoText(text);
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void startGame(CC_CourseData courseData, Map<Long, Player> players, long localPlayerId) {
        System.out.println("Starting game...");
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
        gameController = new GameController(board, localClient, serverDataManager);
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
    /*public void loadGame(File loadedFile) throws GameLoadingException {
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
        gameController = new GameController(newBoard, null, serverDataManager);
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
    }*/

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

    public void quitGame(boolean withPrompt) {
        if (withPrompt) {
            Optional<ButtonType> result = AlertUtils.showConfirmationAlert(
                    "Exit RoboRally?",
                    "Are you sure you want to exit RoboRally?"
            );

            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        disconnectFromServer("", 0);
        roboRally.exitApplication();
    }

    public boolean isGameRunning() {
        return gameController != null;
    }

    public void goToMultiplayerMenu() {
        roboRally.goToMultiplayerMenu();
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void createCourseCreator() {
        roboRally.createCourseCreator();
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

    public void disconnectFromServer(String s, int i) {
        serverDataManager.disconnectFromServer(s, i);
    }

    /**
     * Updates when data received from the server has changed.
     * @param subject the subject who is notifying.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    @Override
    public void update(Subject subject) {
        if (subject.equals(serverDataManager)) {
            if (isGameRunning()) {
                if (!serverDataManager.isConnectedToServer()) {
                    roboRally.goToMainMenu();
                    return;
                }

                gameController.updateGameWithLatestData();
            }
        }
    }

    public void resetGameController() {
        gameController = null;
    }
}
