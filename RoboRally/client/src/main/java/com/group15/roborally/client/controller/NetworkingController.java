package com.group15.roborally.client.controller;

import com.group15.observer.Observer;
import com.group15.observer.Subject;
import com.group15.roborally.client.coursecreator.CC_CourseData;
import com.group15.roborally.client.model.ActionWithDelay;
import com.group15.roborally.client.view.MultiplayerMenuView;
import com.group15.roborally.server.model.Game;
import com.group15.roborally.server.model.GamePhase;
import com.group15.roborally.server.model.Player;
import com.group15.roborally.server.model.Register;
import com.group15.roborally.server.utils.ServerCommunication;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static com.group15.roborally.client.BoardOptions.NO_OF_PLAYERS;

/**
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class NetworkingController extends Subject implements Observer {
    private final AppController appController;
    private final ServerCommunication serverCommunication = new ServerCommunication("http://localhost:8080"); // Remote server: 129.151.221.13
    private ScheduledExecutorService lobbyUpdateScheduler;
    private final Random random = new Random();

    private Game game;
    private List<Player> players;
    private List<Register> registers;
    private Player localPlayer;
    private boolean isHost = false;
    private CC_CourseData selectedCourse = null;
    private boolean hasStartedGameLocally = false; // Condition to keep the application from starting the game more than once per lobby.

    public NetworkingController(AppController appController) {
        this.appController = appController;
        serverCommunication.attach(this);
    }

    /**
     * Calls the server and requests to create a new game.
     * Adds delay in between messages to be able to read them.
     * Adds a random delay after creating the game.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void tryCreateAndJoinGame(MultiplayerMenuView multiplayerMenuView, String playerName) {
        appController.setInfoText("Creating new game...");
        AtomicLong gameId = new AtomicLong();
        runActionAndCallback(new ActionWithDelay(() -> gameId.set(serverCommunication.createGame()), random.nextInt(125, 500)), () -> {
            if (gameId.get() != -1) {
                runActionAndCallback(new ActionWithDelay(() -> {
                    appController.setInfoText("Successfully created new game!");
                }, 500), () -> {
                    tryJoinGameWithGameID(multiplayerMenuView, gameId.get(), playerName);
                });
            } else {
                runActionAndCallback(new ActionWithDelay(() -> {
                    appController.setInfoText("Failed to create new game.");
                }, 1500), () -> {
                    appController.setInfoText("");
                });
            }
        });
    }

    /**
     * Calls the server and requests to join a game.
     * Adds delay in between messages to be able to read them.
     * Adds a random delay after joining the game.
     * @param gameId The ID of the game.
     * @param playerName The name that the player wants.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void tryJoinGameWithGameID(MultiplayerMenuView multiplayerMenuView, long gameId, String playerName) {
        appController.setInfoText("Joining game...");
        AtomicReference<Player> player = new AtomicReference<>();
        runActionAndCallback(new ActionWithDelay(() -> player.set(serverCommunication.joinGame(gameId, playerName)), random.nextInt(125, 500)), () -> {
            if (player.get() != null) {
                runActionAndCallback(new ActionWithDelay(() -> {
                    appController.setInfoText("Successfully joined game!");
                    connectedToGame(multiplayerMenuView, gameId, player.get());
                    appController.showLobby(true);
                }, 500), () -> {
                    appController.setInfoText("");
                });
            } else {
                runActionAndCallback(new ActionWithDelay(() -> {
                    appController.setInfoText("Failed to join game.");
                }, 1500), () -> {
                    appController.setInfoText("");
                });
            }
        });
    }

    // Lobby methods
    /**
     * Initializes the multiplayer menu with the server data for the local player. Is called when the server tells the player they can join the game.
     * @param gameId The game's gameId received from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void connectedToGame(MultiplayerMenuView multiplayerMenuView, long gameId, Player localPlayer) {
        Game game = serverCommunication.getGame(gameId);
        List<Player> players = serverCommunication.getPlayers(gameId);
        hasStartedGameLocally = false;
        this.localPlayer = localPlayer;
        this.game = game;
        this.players = players;
        if (localPlayer.getPlayerId() == game.getHostId()) {
            this.isHost = true;
        }
        multiplayerMenuView.setupLobby(this, game, players, localPlayer, appController.getCourses(), isHost);
        updateFromServer(multiplayerMenuView, game, players, true);
        startUpdateGameLoop(multiplayerMenuView);
    }

    public void changeRobot(String robotName) {
        this.localPlayer.setRobotName(robotName);
        String serverResponse = serverCommunication.updatePlayer(this.localPlayer);
        if (serverResponse != null) System.out.println(serverResponse);
    }

    public void setIsReady(int isReady) {
        this.localPlayer.setIsReady(isReady);
        String serverResponse = serverCommunication.updatePlayer(this.localPlayer);
        if (serverResponse != null) System.out.println(serverResponse);
    }

    public void changeCourse(CC_CourseData course) {
        selectedCourse = course;
        game.setCourseName(selectedCourse.getCourseName());
        String serverResponse = serverCommunication.updateGame(game);
        if (serverResponse != null) System.out.println(serverResponse);
    }

    public void setGameStart() {
        game.setPhase(GamePhase.PROGRAMMING);
        String serverResponse = serverCommunication.updateGame(game);
        if (serverResponse != null) System.out.println(serverResponse);
    }

    public void startPoll(Runnable e, boolean pollExitCondition, Runnable exitAction){
        ScheduledExecutorService serverPoller = Executors.newScheduledThreadPool(1);
        Runnable poll = () -> {
            e.run();
            if(pollExitCondition){
                serverPoller.shutdownNow();
                Platform.runLater(exitAction);
            }
        };
        serverPoller.scheduleAtFixedRate(poll, 1, 100, TimeUnit.MILLISECONDS);
    }

    public void startUpdateGameLoop(MultiplayerMenuView multiplayerMenuView) {
        Runnable lobbyUpdate = () -> {
            Game currentGameData = game;
            if (serverCommunication.getIsConnectedToServer()) {
                if (currentGameData == null) return;

                long gameId = currentGameData.getGameId();
                Game updatedGameData = serverCommunication.getGame(gameId);
                List<Player> updatedPlayers = serverCommunication.getPlayers(gameId);

                if (updatedGameData != null && updatedPlayers != null) {
                    Platform.runLater(() -> updateFromServer(multiplayerMenuView, updatedGameData, updatedPlayers, false));
                }
            }
        };
        lobbyUpdateScheduler = Executors.newScheduledThreadPool(1);
        lobbyUpdateScheduler.scheduleAtFixedRate(lobbyUpdate, 1, 100, TimeUnit.MILLISECONDS);
    }

    public void stopLobbyUpdateLoop() {
        if (lobbyUpdateScheduler != null) {
            lobbyUpdateScheduler.shutdownNow();
        }
        lobbyUpdateScheduler = null;
    }

    public void updateFromServer(MultiplayerMenuView multiplayerMenuView, Game updatedGameData, List<Player> updatedPlayers, boolean isFirstUpdate) {
        // Check if any change has happened
        boolean hasChanges =
                isFirstUpdate ||
                        this.game.hasChanged(updatedGameData) ||
                        this.players.size() != updatedPlayers.size() ||
                        IntStream.range(0, updatedPlayers.size()).anyMatch(i ->
                                this.players.get(i) == null ||
                                        updatedPlayers.get(i) == null ||
                                        this.players.get(i).hasChanged(updatedPlayers.get(i)));
        if (!hasChanges) return;

        switch (game.getPhase()) {
            case LOBBY -> updateLobby(multiplayerMenuView, updatedGameData, updatedPlayers);
            case INITIALIZATION -> updateInitialization(updatedGameData, updatedPlayers);
        }
    }

    public void updateLobby(MultiplayerMenuView multiplayerMenuView, Game updatedGameData, List<Player> updatedPlayers) {
        if (serverCommunication.getIsConnectedToServer()) {
            boolean hostIsConnected = updatedPlayers.stream().anyMatch(player -> updatedGameData.getHostId() == player.getPlayerId());
            if (hostIsConnected) {
                // Check for start game
                if (updatedGameData.getPhase() != GamePhase.LOBBY) {
                    if (!hasStartedGameLocally) {
                        hasStartedGameLocally = true;
                        appController.startGame(selectedCourse, updatedPlayers, localPlayer);
                    }
                }

                // If the game had changes, the player gets set to not ready.
                if (this.game != null) {
                    if (this.game.hasChanged(updatedGameData)) {
                        setIsReady(0);
                    }
                }
                // Variables
                this.game = updatedGameData;
                this.players = updatedPlayers;
                NO_OF_PLAYERS = this.game.getNrOfPlayers();
                // Course
                for (CC_CourseData course : appController.getCourses()) {
                    if (course.getCourseName().equals(game.getCourseName())) {
                        this.selectedCourse = course;
                        break;
                    }
                }
                multiplayerMenuView.updateLobby(this, this.game, this.players, this.localPlayer, this.selectedCourse);
            } else {
                disconnectFromServer("The host left the game.", 3000);
            }
        }
    }

    public void updateInitialization(Game updatedGameData, List<Player> updatedPlayers) {
        this.game = updatedGameData;
        this.players = updatedPlayers;
        NO_OF_PLAYERS = this.game.getNrOfPlayers();
        notifyChange();
    }

    /**
     * Method for manually leaving the server and game.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void disconnectFromServer(String infoMessage, int showMessageTimeInMillis) {
        if (serverCommunication.getIsConnectedToServer()) {
            if (infoMessage == null || infoMessage.isEmpty()) {
                infoMessage = "Disconnected from server.";
            }
            String finalInfoMessage = infoMessage;
            runActionAndCallback(new ActionWithDelay(() -> {
                appController.setInfoText(finalInfoMessage);
                serverCommunication.deletePlayer(this.localPlayer);
                appController.leftGame();
                //appController.showLobby(false);
                stopLobbyUpdateLoop();
            }, showMessageTimeInMillis), () -> {
                appController.setInfoText("");
            });
        }
    }

    /**
     * Method for when the connection to the server was lost, and the reconnection timed out.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void connectionToServerTimedOut() {
        runActionAndCallback(new ActionWithDelay(() -> {
            appController.setInfoText("Connection to server timed out.");
            appController.showLobby(false);
            stopLobbyUpdateLoop();
        }, 2000), () -> {
            appController.setInfoText("");
        });
    }

    private void runActionAndCallback(ActionWithDelay actionWithDelay, Runnable callback) {
        actionWithDelay.getAction(false).run();
        PauseTransition pause = new PauseTransition(Duration.millis(actionWithDelay.getDelayInMillis()));
        pause.setOnFinished(event -> callback.run());
        pause.play();
    }

    @Override
    public void update(Subject subject) {
        // If the player was disconnected from the server.
        if (!serverCommunication.getIsConnectedToServer()) {
            connectionToServerTimedOut();
        }
    }

    public void updateRegister(String playerName, String[] regiserMoves, int turn) {
        Player player = getPlayer(playerName);
        serverCommunication.updateRegister(regiserMoves, player.getPlayerId(), turn);
    }



    public Player getPlayer(String playerName) {
        return players.stream().filter(player -> player.getPlayerName().equals(playerName)).findFirst().orElse(null);
    }

    public Game getUpdatedGame() {
        return game;
    }

    public List<Player> getUpdatedPlayers() {
        return players;
    }

    public CC_CourseData getUpdatedSelectedCourse() {
        return selectedCourse;
    }

    public void updateRegisters(long gameId, Runnable callback) {
        registers = null;
        ScheduledExecutorService serverPoller = Executors.newScheduledThreadPool(1);
        Runnable poll = () -> {
            registers = serverCommunication.getRegisters(gameId);
            if (registers != null) {
                serverPoller.shutdownNow();
                Platform.runLater(callback);
            }
        };
        serverPoller.scheduleAtFixedRate(poll, 1, 100, TimeUnit.MILLISECONDS);
    }

    public String[] getRegisters(String playerName) {
        if (registers != null) {
            Player player = getPlayer(playerName);
            if (player != null) {
                Register register = registers.stream().filter(r -> r.getPlayerId() == player.getPlayerId()).findFirst().orElse(null);
                if (register != null) {
                    return register.getMoves();
                }
            }
        }
        return null;
    }
}
