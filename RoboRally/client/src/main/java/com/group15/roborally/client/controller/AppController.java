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

import com.group15.roborally.client.observer.Observer;
import com.group15.roborally.client.observer.Subject;
import com.group15.roborally.client.RoboRally;
import com.group15.roborally.communication.ServerCommunication;
import com.group15.roborally.coursecreator.CC_CourseData;
import com.group15.roborally.coursecreator.CC_JsonUtil;
import com.group15.roborally.exceptions.EmptyCourseException;
import com.group15.roborally.exceptions.GameLoadingException;
import com.group15.roborally.exceptions.NoCoursesException;
import com.group15.model.lobby.*;
import com.group15.roborally.client.utils.Adapter;
import com.group15.roborally.client.utils.SaveAndLoadUtils;
import com.group15.roborally.client.templates.BoardTemplate;
import com.group15.model.boardelements.BoardElement;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.group15.roborally.client.BoardOptions.*;


/**
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class AppController implements Observer {
    private final RoboRally roboRally;
    public boolean isCourseCreatorOpen = false;
    private GameController gameController;
    private final ServerCommunication serverCommunication;
    private ScheduledExecutorService lobbyUpdateScheduler;

    private List<CC_CourseData> courses = new ArrayList<>();

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
        this.serverCommunication = new ServerCommunication();
    }

    /**
     * Method for going to the join/host multiplayer menu.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void initializeMultiplayerMenu() {
        roboRally.createMultiplayerMenu();
    }

    /**
     * Method for the player to host a new lobby.
     * Calls the server and requests to host a new game.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void tryHostNewLobby(String playerName) {
        LobbyData lobbyData = serverCommunication.createLobby(playerName);
        roboRally.connectedToLobby(lobbyData);
    }

    /**
     * Method for a player to join an existing lobby.
     * @param gameId The gameID of the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void tryJoinLobbyWithGameID(String gameId, String playerName) {
        LobbyData lobbyData = serverCommunication.joinLobby(gameId, playerName);
        roboRally.connectedToLobby(lobbyData);
    }

    public void changeRobot(LobbyData lobbyData, String robotName) {
        updateLobby(serverCommunication.changeRobot(lobbyData, robotName));
    }

    public void changeCourse(LobbyData lobbyData, CC_CourseData chosenCourse) {
        updateLobby(serverCommunication.changeCourse(lobbyData,chosenCourse.getCourseName()));
    }

    public void toggleIsReady(LobbyData lobbyData) {
        // Toggling whether the player is ready.
        int isReady = lobbyData.areReady()[0] == 0 ? 1 : 0;
        if (lobbyData.areReady()[0] == 1) lobbyData.areReady()[0] = 0;

        updateLobby(serverCommunication.setIsReady(lobbyData, isReady));
    }

    public void startLobbyUpdateLoop() {
        Runnable lobbyUpdate = () -> {
            // TODO: Potential issue with de-sync of local LobbyData. Should be investigated.
            LobbyData updatedLobbyServerReceive;
            if (serverCommunication.getIsConnectedToServer()) {
                updatedLobbyServerReceive = serverCommunication.getUpdatedLobby(roboRally.getCurrentLobbyData());
            } else {
                updatedLobbyServerReceive = null;
            }
            if (updatedLobbyServerReceive == null) {
                System.out.println("Disconnected from server.");
                roboRally.goToMainMenu();
            } else {
                System.out.println(updatedLobbyServerReceive);
            }
            Platform.runLater(() -> updateLobby(updatedLobbyServerReceive));
        };
        lobbyUpdateScheduler = Executors.newScheduledThreadPool(1);
        lobbyUpdateScheduler.scheduleAtFixedRate(lobbyUpdate, 1, 5, TimeUnit.SECONDS);
    }
    public void stopLobbyUpdateLoop() {
        lobbyUpdateScheduler.close();
        lobbyUpdateScheduler = null;
    }

    public void updateLobby(LobbyData updatedLobbyData) {
        if (serverCommunication.getIsConnectedToServer()) {
            roboRally.updateLobby(updatedLobbyData);
        }
    }

    /**
     *
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void disconnectFromServer() {
        if (serverCommunication.getIsConnectedToServer()) {
            stopLobbyUpdateLoop();
            serverCommunication.leaveGame(roboRally.getCurrentLobbyData().playerId());
        }
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void beginCourse(CC_CourseData courseData, LobbyData lobbyData) {
        Pair<List<Space[][]>, Space[][]> courseSpaces = courseData.getGameSubBoards();
        Board board = new Board(courseSpaces.getKey(), courseSpaces.getValue(), courseData.getCourseName(), courseData.getNoOfCheckpoints());

        // GameController
        gameController = new GameController(board, this::gameOver);

        // Adding players
        for (int i = 0; i < NO_OF_PLAYERS; i++) {
            Player player = new Player(board, Objects.requireNonNull(Robots.getRobotByName(lobbyData.robotNames()[i])), lobbyData.playerNames()[i]);
            player.setHeading(Heading.EAST);
            board.addPlayer(player);
        }

        board.setCurrentPlayer(board.getPlayer(0));

        roboRally.createBoardView(gameController);
    }

    /**
     * Load a game from a file. A new game controller is created, with the board loaded
     * from the file. The Phase of the game is manually set to Programming.
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
        gameController = new GameController(newBoard, this::gameOver);
        SaveAndLoadUtils.loadPlayers(boardTemplate, newBoard, gameController);
        newBoard.setCurrentPhase(Phase.PROGRAMMING);

        // Players
        for (int i = 0; i < newBoard.getNoOfPlayers(); i++) {
            Player player = newBoard.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_OF_REGISTERS; j++) {
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
        if (gameController.board.getCurrentPhase() != Phase.PROGRAMMING) {
            return false;
        }
        SaveAndLoadUtils.saveBoard(gameController.board, file);
        return true;
    }

    /**
     * sets ends game
     * @author Maximillian Bjørn Mortensen
     */
    public void gameOver() {
        roboRally.goToWinScreen(gameController);
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

    /**
     * sets game controller to null
     * @param gameController The gameController.
     * @author Maximillian Bjørn Mortensen
     */
    public void setGameController(GameController gameController){
        this.gameController = gameController;
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

    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
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

    /**
     * @return The loaded courses.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public List<CC_CourseData> getCourses() {
        return courses;
    }
}
