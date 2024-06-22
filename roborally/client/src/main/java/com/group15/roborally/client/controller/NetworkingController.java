package com.group15.roborally.client.controller;

import com.group15.observer.Observer;
import com.group15.observer.Subject;
import com.group15.roborally.client.coursecreator.CC_CourseData;
import com.group15.roborally.client.model.ActionWithDelay;
import com.group15.roborally.client.model.CardField;
import com.group15.roborally.client.model.Space;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCard;
import com.group15.roborally.client.view.MultiplayerMenuView;
import com.group15.roborally.server.model.Game;
import com.group15.roborally.server.model.GamePhase;
import com.group15.roborally.server.model.Player;
import com.group15.roborally.server.model.Register;
import com.group15.roborally.client.utils.ServerCommunication;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.group15.roborally.client.BoardOptions.NO_OF_PLAYERS;

/**
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class NetworkingController extends Subject implements Observer {
    private final AppController appController;
    private final ServerCommunication serverCommunication = new ServerCommunication();
    private ScheduledExecutorService gameUpdateScheduler;
    private ScheduledExecutorService serverPoller;

    private final Random random = new Random();
    private boolean hasStartedGameLocally = false; // Condition to keep the application from starting the game more than once per lobby.

    @Getter
    private boolean isHost = false;
    @Getter
    private CC_CourseData selectedCourse = null;

    // Updated Game data
    @Getter
    private Player localPlayer;
    @Getter
    private final HashMap<Long, Player> playerMap = new HashMap<>();
    @Getter
    private List<Player> players;
    @Getter
    private String[] upgradeShop;
    private Game game;
    private List<Register> registers;

    public NetworkingController(AppController appController) {
        this.appController = appController;
        serverCommunication.attach(this);
    }

    // Server queries.
    /**
     * Calls the server and requests to create a new game.
     * Adds delay in between messages to be able to read them.
     * Adds a random delay after creating the game.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void tryCreateAndJoinGame(MultiplayerMenuView multiplayerMenuView, String playerName) {
        appController.setInfoText("Creating new game...");
        AtomicLong gameId = new AtomicLong();
        runActionAndCallback(new ActionWithDelay(() -> gameId.set(serverCommunication.createGame(multiplayerMenuView.getServerURLInput())), random.nextInt(125, 500)), () -> {
            if (gameId.get() != -1) {
                runActionAndCallback(new ActionWithDelay(
                        () -> appController.setInfoText("Successfully created new game!"), 500),
                        () -> tryJoinGameWithGameID(multiplayerMenuView, gameId.get(), playerName));
            } else {
                runActionAndCallback(new ActionWithDelay(
                        () -> appController.setInfoText("Failed to create new game."),1500),
                        () -> appController.setInfoText(""));
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
        runActionAndCallback(new ActionWithDelay(() -> player.set(serverCommunication.joinGame(multiplayerMenuView.getServerURLInput(), gameId, playerName)), random.nextInt(125, 500)), () -> {
            if (player.get() != null) {
                runActionAndCallback(new ActionWithDelay(() -> {
                    appController.setInfoText("Successfully joined game!");
                    connectedToGame(multiplayerMenuView, gameId, player.get());
                    appController.showLobby(true);
                }, 500), () -> appController.setInfoText(""));
            } else {
                runActionAndCallback(new ActionWithDelay(
                        () -> appController.setInfoText("Failed to join game."), 1500),
                        () -> appController.setInfoText(""));
            }
        });
    }


    /**
     * Initializes the multiplayer menu with the server data for the local player. Is called when the server tells the player they can join the game.
     * @param gameId The game's gameId received from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void connectedToGame(MultiplayerMenuView multiplayerMenuView, long gameId, Player localPlayer) {
        Game game = serverCommunication.getGame(gameId);
        List<Player> players = serverCommunication.getPlayers(gameId);
        this.hasStartedGameLocally = false;
        this.selectedCourse = null;
        this.localPlayer = localPlayer;
        this.isHost = localPlayer.getPlayerId() == game.getHostId();
        updateGameData(game, players, null);
        multiplayerMenuView.setupLobby(this, game, players, this.localPlayer, appController.getCourses(), this.isHost);
        updateFromServer(multiplayerMenuView);
        startUpdateGameLoop(multiplayerMenuView);
    }

    /**
     * Starts a loop of fetching game data from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void startUpdateGameLoop(MultiplayerMenuView multiplayerMenuView) {
        Runnable lobbyUpdate = () -> {
            Game currentGameData = this.game;
            if (serverCommunication.isConnectedToServer()) {
                if (currentGameData == null) return;

                updateFromServer(multiplayerMenuView);
            }
        };
        gameUpdateScheduler = Executors.newScheduledThreadPool(1);
        gameUpdateScheduler.scheduleAtFixedRate(lobbyUpdate, 1, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Method for manually leaving the server and game.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void disconnectFromServer(String infoMessage, int showMessageTimeInMillis) {
        if (serverCommunication.isConnectedToServer()) {
            if (infoMessage == null || infoMessage.isEmpty()) {
                infoMessage = "Disconnected from server.";
            }
            String finalInfoMessage = infoMessage;
            runActionAndCallback(new ActionWithDelay(() -> {
                appController.setInfoText(finalInfoMessage);
                serverCommunication.deletePlayer(this.localPlayer);
                appController.leftGame();
                stopGameUpdateLoop();
            }, showMessageTimeInMillis), () -> appController.setInfoText(""));
        }
    }

    /**
     * Stops the loop of updating the client with data from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void stopGameUpdateLoop() {
        if (gameUpdateScheduler != null) {
            gameUpdateScheduler.shutdownNow();
        }
        gameUpdateScheduler = null;
    }

    public void updateRegisters(Runnable callback) {
        this.registers = null;
        Runnable poll = () -> {
            // System.out.println("Polling server for registers.");
            this.registers = serverCommunication.getRegisters(game.getGameId());
            if (this.registers != null) {
                // System.out.println("Registers received");
                serverPoller.shutdownNow();
                Platform.runLater(callback);
            }
        };
        serverPoller = Executors.newScheduledThreadPool(1);
        serverPoller.scheduleAtFixedRate(poll, 1, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * Updates the client with data from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void updateFromServer(MultiplayerMenuView multiplayerMenuView) {
        Game updatedGame = serverCommunication.getGame(this.game.getGameId());
        List<Player> updatedPlayers = serverCommunication.getPlayers(this.game.getGameId());

        String[] updatedUpgradeShop;
        if (updatedGame.getPhase() == GamePhase.UPGRADE) {
            updatedUpgradeShop = serverCommunication.getUpgradeShop(this.game.getGameId());
        } else {
            updatedUpgradeShop = null;
        }

        if (updatedPlayers != null) {
            System.out.println();
            for (Player player : updatedPlayers) {
                System.out.println("Got " + player.getPlayerName() + " with id " + player.getPlayerId() + " with robot " + player.getRobotName());
            }
            Platform.runLater(() -> {
                // Check if host has disconnected
                boolean hostHasDisconnected = updatedPlayers.stream().noneMatch(player -> updatedGame.getHostId() == player.getPlayerId());
                if (hostHasDisconnected) {
                    disconnectFromServer("The host left the game.", 3000);
                    return;
                }

                if (updatedGame.getPhase().equals(GamePhase.LOBBY)) {
                    // Update lobby.
                    updateLobby(multiplayerMenuView, updatedGame, updatedPlayers);
                } else {
                    // Check if game started
                    if (!hasStartedGameLocally) {
                        hasStartedGameLocally = true;
                        updateLobby(multiplayerMenuView, updatedGame, updatedPlayers);
                        appController.startGame(selectedCourse, updatedPlayers, localPlayer);

                    } else {
                        // Update the game with data received from the server and notify observers.
                        updateGameData(updatedGame, updatedPlayers, updatedUpgradeShop);
                    }
                }
            });
        }
    }

    /**
     * Updates the lobby with data from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void updateLobby(MultiplayerMenuView multiplayerMenuView, Game updatedGame, List<Player> updatedPlayers) {
        if (serverCommunication.isConnectedToServer()) {
            // If the game had changes, the player gets set to not ready.
            if (this.game != null) {
                if (this.game.hasChanged(updatedGame)) {
                    setIsReady(0);
                }
            }
            // Update the game with data received from the server and notify observers.
            updateGameData(updatedGame, updatedPlayers, null);
            // Update selected course if it changed
            if (updatedGame.getCourseName() != null && !updatedGame.getCourseName().isBlank() && (this.selectedCourse == null || !updatedGame.getCourseName().equals(this.selectedCourse.getCourseName()))) {
                this.selectedCourse = appController.getCourses().stream()
                        .filter(course -> course.getCourseName().equals(updatedGame.getCourseName()))
                        .findFirst()
                        .orElse(null);
            }
            // Update the lobby with the updated variables.
            multiplayerMenuView.updateLobby(this, this.game, this.players, this.selectedCourse);
        }
    }

    /**
     * Updates the GameController with data from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void updateGameData(Game updatedGame, List<Player> updatedPlayers, String[] updatedUpgradeShop) {
        if (updatedGame != null) {
            this.game = updatedGame;
        }
        if (updatedPlayers != null) {
            this.players = updatedPlayers;
            NO_OF_PLAYERS = this.game.getNrOfPlayers();
            Map<Long, Player> updatedPlayerMap = updatedPlayers.stream()
                    .collect(Collectors.toMap(Player::getPlayerId, player -> player));
            this.playerMap.clear();
            this.playerMap.putAll(updatedPlayerMap);
            //this.localPlayer = this.playerMap.get(this.localPlayer.getPlayerId());
        }
        if (updatedUpgradeShop != null) {
            this.upgradeShop = updatedUpgradeShop;
        }
        notifyChange();
    }

    /**
     * Runs the ActionWithDelay runnable, followed by the delay pause, then calls the callback runnable.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void runActionAndCallback(ActionWithDelay actionWithDelay, Runnable callback) {
        actionWithDelay.getAction(false).run();
        PauseTransition pause = new PauseTransition(Duration.millis(actionWithDelay.getDelayInMillis()));
        pause.setOnFinished(_ -> callback.run());
        pause.play();
    }

    /**
     * Method for when the connection to the server was lost, and the reconnection timed out.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void connectionToServerTimedOut() {
        runActionAndCallback(new ActionWithDelay(() -> {
            appController.setInfoText("Connection to server timed out.");
            appController.showLobby(false);
            stopGameUpdateLoop();
        }, 2000), () -> {
            appController.setInfoText("");
            appController.leftGame();
        });
    }



    // Update data and send to server
    // Lobby
    public void changeRobot(String robotName) {
        this.localPlayer.setRobotName(robotName);
        System.out.println("setting robot to " + robotName);
        serverCommunication.updatePlayer(this.localPlayer);
    }
    public void setIsReady(int isReady) {
        this.localPlayer.setIsReady(isReady);
        serverCommunication.updatePlayer(this.localPlayer);
    }
    public void changeCourse(CC_CourseData course) {
        this.selectedCourse = course;
        this.game.setCourseName(selectedCourse.getCourseName());
        serverCommunication.updateGame(game);
    }
    public void setGamePhase(GamePhase gamePhase) {
        if (isHost) {
            this.game.setPhase(gamePhase);
            serverCommunication.updateGame(game);
        }
    }
    // In game
    public void updateRegister(long playerId, String[] registerMoves, int turn) {
        Player player = playerMap.get(playerId);
        serverCommunication.updateRegister(registerMoves, player.getPlayerId(), turn);
    }
    public void setPlayerSpawn(Space space, String directionName) {
        if (this.localPlayer.getSpawnDirection() == null || this.localPlayer.getSpawnDirection().isBlank()) {
            this.localPlayer.setSpawnPoint(new int[]{space.x, space.y});
            this.localPlayer.setSpawnDirection(directionName);
            serverCommunication.updatePlayer(this.localPlayer);
        }
    }
    public void updateUpgradeShop(CardField[] availableCardsFields) {
        String[] availableCards = new String[availableCardsFields.length];
        for (int i = 0; i < availableCards.length; i++) {
            availableCards[i] = ((UpgradeCard)availableCardsFields[i].getCard()).getEnum().name();
        }
        serverCommunication.updateUpgradeShop(availableCards, this.game.getGameId());
    }
    void updatePlayerUpgradeCards(String[] permCards, String[] tempCards) {
        this.localPlayer.setPermCards(permCards);
        this.localPlayer.setTempCards(tempCards);
        serverCommunication.updatePlayer(this.localPlayer);
    }


    // Getters. Game data variables are updated continually.
    public String[] getRegistersFromPlayer(long playerId) {
        if (this.registers != null) {
            Register register = this.registers.stream().filter(r -> (r.getPlayerId() == playerId)).findFirst().orElse(null);

            if (register != null) {
                return register.getMoves();
            }
        }
        return null;
    }




    @Override
    public void update(Subject subject) {
        // If the player was disconnected from the server.
        if (!serverCommunication.isConnectedToServer()) {
            connectionToServerTimedOut();
        }
    }

    public void updatePhase(GamePhase phase) {
        if (isHost) {
            setGamePhase(phase);
        }
    }
}
