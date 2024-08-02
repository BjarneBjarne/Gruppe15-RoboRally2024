package com.group15.roborally.client.model.networking;

import com.group15.roborally.client.RoboRally;
import com.group15.roborally.common.model.*;
import com.group15.roborally.common.observer.Observer;
import com.group15.roborally.common.observer.Subject;
import com.group15.roborally.client.controller.AppController;
import com.group15.roborally.client.coursecreator.CC_CourseData;
import com.group15.roborally.client.model.ActionWithDelay;
import com.group15.roborally.client.model.Space;
import com.group15.roborally.client.model.player_interaction.PlayerInteraction;
import com.group15.roborally.client.utils.NetworkedDataTypes;
import com.group15.roborally.client.utils.ServerCommunication;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import lombok.Getter;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.group15.roborally.client.LobbySettings.NO_OF_PLAYERS;

/**
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class ServerDataManager extends Subject implements Observer {
    private final ServerCommunication serverCommunication = new ServerCommunication();
    private ScheduledExecutorService gameUpdateScheduler;
    private ScheduledExecutorService serverPoller;

    private final Random random = new Random();

    @Getter
    private Player localPlayer = null;
    @Getter
    private boolean isHost = false;

    @Getter
    private Interaction interaction = null;
    @Setter
    private int currentTurnCount, currentPhaseCount, currentWaitCount, currentInteractionCount = -1;
    @Setter
    private GamePhase currentPhase = GamePhase.LOBBY;

    // Updated Game data
    private Game game = null;
    private Map<Long, Player> playerMap = new HashMap<>();
    private UpgradeShop upgradeShop = null;
    private List<Register> registers = null;
    @Getter
    private List<Choice> choices = null;
    @Getter
    private final static List<NetworkedDataTypes> changedData = new ArrayList<>();

    public ServerDataManager() {
        serverCommunication.attach(this);
    }

    // Server queries.
    /**
     * Calls the server and requests to create a new game.
     * Adds delay in between messages to be able to read them.
     * Adds a random delay after creating the game.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void tryCreateAndJoinGame(String serverURL, String playerName) {
        System.out.println();
        AppController.setInfoText("Creating new game...");
        AtomicReference<String> gameId = new AtomicReference<>("");
        runActionAndCallback(new ActionWithDelay(
                () -> gameId.set(serverCommunication.createGame(serverURL)), random.nextInt(0, 100), "Creating new game", false),
                () -> {
                    if (gameId.get() != null && !gameId.get().isBlank()) {
                        runActionAndCallback(new ActionWithDelay(
                                () -> {
                                    AppController.setInfoText("Successfully created new game!");
                                    System.out.println("Game ID: " + gameId.get());
                                }, 250, "Successfully created new game", false),
                                () -> tryJoinGameWithGameID(serverURL, gameId.get(), playerName));
                    } else {
                        runActionAndCallback(new ActionWithDelay(
                                () -> AppController.setInfoText("Failed to create new game."),1500, "Failed to create new game", false),
                                () -> AppController.setInfoText(""));
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
    public void tryJoinGameWithGameID(String serverURL, String gameId, String playerName) {
        System.out.println();
        AppController.setInfoText("Joining game...");
        AtomicReference<Player> player = new AtomicReference<>();
        runActionAndCallback(new ActionWithDelay(
                () -> player.set(serverCommunication.joinGame(serverURL, gameId, playerName)), random.nextInt(0, 100), "Joining game with gameId: \"" + gameId + "\" with playerName: \"" + playerName + "\".", false),
                () -> {
                    if (player.get() != null) {
                        runActionAndCallback(new ActionWithDelay(
                                () -> {
                                      AppController.setInfoText("Successfully joined game!");
                                      connectedToGame(gameId, player.get());
                                }, 250, "Successfully joined game", false),
                                () -> AppController.setInfoText(""));
                    } else {
                        runActionAndCallback(new ActionWithDelay(
                                () -> AppController.setInfoText("Failed to join game."), 1500, "Failed to join game", false),
                                () -> AppController.setInfoText(""));
                    }
        });
    }


    /**
     * Initializes the multiplayer menu with the server data for the local player. Is called when the server tells the player they can join the game.
     * @param gameId The game's gameId received from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void connectedToGame(String gameId, Player localPlayer) {
        // Resetting variables
        this.localPlayer = localPlayer;
        this.isHost = false;
        this.interaction = null;
        this.currentTurnCount = 0;
        this.currentWaitCount = 0;
        this.currentInteractionCount = 0;
        this.currentPhaseCount = -1;
        this.currentPhase = GamePhase.LOBBY;
        // Resetting game data
        this.game = null;
        this.playerMap = new HashMap<>();
        this.upgradeShop = null;
        this.registers = null;
        this.choices = null;
        changedData.clear();

        updateGameData(serverCommunication.getGame(gameId));
        updatePlayerData(serverCommunication.getPlayers(gameId));
        updateUpgradeShopData(serverCommunication.getUpgradeShop(gameId));

        // Setting up data
        Platform.runLater(() -> {
            this.isHost = localPlayer.getPlayerId() == game.getHostId();
            setReadyForPhase(GamePhase.LOBBY);
            startUpdateGameLoop();
            notifyChange();
            System.out.println("Player name: " + localPlayer.getPlayerName());
            System.out.println("Is host? " + this.isHost);
        });
    }

    /**
     * Starts a loop of fetching game data from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void startUpdateGameLoop() {
        Runnable lobbyUpdate = () -> {
            if (isConnectedToServer()) {
                if (this.game == null) {
                    System.err.println("THIS.GAME IS NULL");
                    return;
                }
                try {
                    updateGameFromServerData();
                } catch (Exception e) {
                    System.err.println("Error in update game loop. Exception message: " + e.getMessage());
                }
            }
        };
        gameUpdateScheduler = Executors.newScheduledThreadPool(10);
        gameUpdateScheduler.scheduleAtFixedRate(lobbyUpdate, 1, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the loop of updating the client with data from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void stopUpdateLoops() {
        if (gameUpdateScheduler != null) {
            gameUpdateScheduler.shutdownNow();
            gameUpdateScheduler = null;
        }
        if (serverPoller != null) {
            serverPoller.shutdownNow();
            serverPoller = null;
        }
    }

    /**
     * Updates the client with data from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void updateGameFromServerData() {
        // Get updated game data.
        Game updatedGame = serverCommunication.getGame(game.getGameId());
        if (updatedGame == null) {
            System.err.println("GAME IS NULL");
            return;
        }
        Map<Long, Player> updatedPlayers = serverCommunication.getPlayers(game.getGameId());
        if (updatedPlayers == null) {
            System.err.println("PLAYER LIST IS NULL");
            return;
        }
        UpgradeShop updatedUpgradeShop;
        if (currentPhase.equals(GamePhase.UPGRADE)) {
            updatedUpgradeShop = serverCommunication.getUpgradeShop(game.getGameId());
        } else {
            updatedUpgradeShop = null;
        }
        List<Register> updatedRegisters;
        if (currentPhase.equals(GamePhase.PROGRAMMING) || currentPhase.equals(GamePhase.PLAYER_ACTIVATION)) {
            updatedRegisters = serverCommunication.getRegisters(game.getGameId(), currentTurnCount);
        } else {
            updatedRegisters = null;
        }
        List<Choice> updatedChoices = serverCommunication.getChoices(game.getGameId(), currentWaitCount);

        // Check if the host has disconnected
        boolean hostHasDisconnected = updatedPlayers.get(updatedGame.getHostId()) == null;
        if (hostHasDisconnected) {
            System.out.println("updatedGame.getHostId(): " + updatedGame.getHostId());
            for (Player player : updatedPlayers.values()) {
                System.out.println("player: " + player.getPlayerName() + ", playerId: " + player.getPlayerId());
            }
            disconnectFromServer("The host left the game.", 3000);
            return;
        }

        loadServerDataAndNotify(updatedGame, updatedPlayers, updatedUpgradeShop, updatedRegisters, updatedChoices);
    }

    /**
     * Loads the data received from the server and notifies listeners if any data has changed.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void loadServerDataAndNotify(Game updatedGame, Map<Long, Player> updatedPlayers, UpgradeShop updatedUpgradeShop, List<Register> updatedRegisters, List<Choice> updatedChoices) {
        boolean hasChanges = false;
        // Update any data that has changed.
        if (this.game.getNrOfPlayers() != updatedGame.getNrOfPlayers() ||
                updatedPlayers.values().stream().anyMatch(updatedPlayer -> updatedPlayer.hasChanges(playerMap.get(updatedPlayer.getPlayerId())))) {
            updatePlayerData(updatedPlayers);
            hasChanges = true;
        }
        if (updatedGame.hasChanges(this.game)) {
            updateGameData(updatedGame);
            hasChanges = true;
        }
        if (updatedUpgradeShop != null) {
            if (updatedUpgradeShop.hasChanges(this.upgradeShop) && updatedUpgradeShop.getTurn() >= this.upgradeShop.getTurn()) {
                updateUpgradeShopData(updatedUpgradeShop);
                hasChanges = true;
            }
        }
        if (updatedRegisters != null) {
            RoboRally.setDebugText(7, "updatedRegisters.size: " + updatedRegisters.size());
            if (this.registers == null ||
                    this.registers.size() != updatedRegisters.size() ||
                    IntStream.range(0, updatedRegisters.size()).anyMatch(i -> updatedRegisters.get(i).hasChanges(this.registers.get(i)))) {
                updateRegisterData(updatedRegisters);
                hasChanges = true;
            }
        } else {
            RoboRally.setDebugText(7, "updatedRegisters are null");
        }
        if (updatedChoices != null) {
            if (!Choice.areListsEqual(this.choices, updatedChoices)) {
                updateChoiceData(updatedChoices);
                hasChanges = true;
            }
        }
        if (hasChanges && isConnectedToServer()) {
            notifyChange();
        }
    }

    private void updateGameData(@NotNull Game updatedGameData) {
        this.game = updatedGameData;
        NO_OF_PLAYERS = updatedGameData.getNrOfPlayers();
        changedData.add(NetworkedDataTypes.GAME);
    }
    private void updatePlayerData(@NotNull Map<Long, Player> updatedPlayerData) {
        this.playerMap = updatedPlayerData;
        changedData.add(NetworkedDataTypes.PLAYERS);
    }
    private void updateUpgradeShopData(@NotNull UpgradeShop updatedUpgradeShop) {
        this.upgradeShop = updatedUpgradeShop;
        changedData.add(NetworkedDataTypes.UPGRADE_SHOP);
    }
    private void updateRegisterData(@NotNull List<Register> updatedRegisters) {
        this.registers = updatedRegisters;
        changedData.add(NetworkedDataTypes.REGISTERS);
    }
    private void updateChoiceData(@NotNull List<Choice> updatedChoices) {
        this.choices = updatedChoices;
        changedData.add(NetworkedDataTypes.CHOICES);
    }

    /**
     * Runs the ActionWithDelay runnable, followed by the delay pause, then calls the callback runnable.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void runActionAndCallback(ActionWithDelay actionWithDelay, Runnable callback) {
        actionWithDelay.runAndCallback(callback);
    }




    // Update data and send to server
    // Lobby
    public void changeRobot(@NotNull String robotName) {
        if (!robotName.equals(localPlayer.getRobotName())) {
            localPlayer.setRobotName(robotName);
            serverCommunication.putPlayer(localPlayer);
        }
    }
    public void setReadyForPhase(@NotNull GamePhase nextPhase) {
        if (!nextPhase.equals(localPlayer.getReadyForPhase())) {
            localPlayer.setReadyForPhase(nextPhase);
            localPlayer.setPhaseCount(currentPhaseCount);
            serverCommunication.putPlayer(localPlayer);
        }
    }
    public void changeCourse(@NotNull CC_CourseData selectedCourse) {
        if (!selectedCourse.getCourseName().equals(game.getCourseName())) {
            Game gameTemp = game.getGameCopy();
            gameTemp.setCourseName(selectedCourse.getCourseName());
            serverCommunication.putGame(gameTemp);
        }
    }
    public void setGamePhase(@NotNull GamePhase gamePhase) {
        if (isHost && !gamePhase.equals(game.getPhase())) {
            Game gameTemp = game.getGameCopy();
            gameTemp.setPhase(gamePhase);
            serverCommunication.putGame(gameTemp);
        }
    }
    // In game
    public void setPlayerRegister(@NotNull String[] programFieldNames) {
        serverCommunication.postRegister(programFieldNames, localPlayer.getPlayerId(), currentTurnCount);
    }
    public void setPlayerSpawn(@NotNull Space space, String directionName) {
        if (localPlayer.getSpawnDirection() == null || localPlayer.getSpawnDirection().isBlank()) {
            localPlayer.setSpawnPoint(new int[]{space.x, space.y});
            localPlayer.setSpawnDirection(directionName);
            serverCommunication.putPlayer(localPlayer);
        }
    }
    public void setUpgradeShop(@NotNull UpgradeShop newUpgradeShop) {
        serverCommunication.putUpgradeShop(newUpgradeShop, game.getGameId(), currentTurnCount);
    }
    public void setPlayerUpgradeCards(@NotNull String[] permCards, @NotNull String[] tempCards) {
        localPlayer.setPermCards(permCards);
        localPlayer.setTempCards(tempCards);
        serverCommunication.putPlayer(localPlayer);
    }



    // Upgrade cards
    public void setChoice(ChoiceDTO choiceDTO) {
        serverCommunication.putChoice(choiceDTO);
        notifyChange();
    }
    public void setReadyChoice() {
        ChoiceDTO emptyChoiceDTO = new ChoiceDTO(game.getGameId(), localPlayer.getPlayerId(), Choice.READY_CHOICE, currentWaitCount, Choice.ResolveStatus.NONE.name());
        serverCommunication.putChoice(emptyChoiceDTO);
    }
    public void waitForChoicesAndCallback(Runnable callback) {
        Runnable poll = () -> {
            List<Choice> choices = serverCommunication.getChoices(game.getGameId(), currentWaitCount);
            Set<Long> choicePlayerIds = choices.stream()
                    .filter(c -> c.getCode().equals(Choice.READY_CHOICE))
                    .map(Choice::getPlayerId)
                    .collect(Collectors.toSet());

            boolean hasAChoiceFromAllPlayers = choicePlayerIds.containsAll(playerMap.keySet());

            if (hasAChoiceFromAllPlayers) {
                this.choices = choices;
                serverPoller.shutdownNow();
                Platform.runLater(callback);
            }
        };
        serverPoller = Executors.newScheduledThreadPool(1);
        serverPoller.scheduleAtFixedRate(poll, 1, 100, TimeUnit.MILLISECONDS);
    }

    // Player interactions
    public void setInteraction(PlayerInteraction currentPlayerInteraction, String interaction) {
        if (currentPlayerInteraction.getPlayer().getPlayerId() != localPlayer.getPlayerId()) return;
        InteractionDTO interactionDTO = new InteractionDTO(localPlayer.getPlayerId(), interaction, currentInteractionCount);
        serverCommunication.putInteraction(interactionDTO);
    }
    public void waitForInteractionAndCallback(Runnable callback, long playerId, int interactionNo) {
        interaction = null;
        RoboRally.setDebugText(12, "Waiting for interaction");
        Runnable poll = () -> {
            interaction = serverCommunication.getInteraction(playerId, interactionNo);
            if (interaction != null) {
                serverPoller.shutdownNow();
                RoboRally.setDebugText(12, "");
                Platform.runLater(callback);
            }
        };
        serverPoller = Executors.newScheduledThreadPool(1);
        serverPoller.scheduleAtFixedRate(poll, 1, 100, TimeUnit.MILLISECONDS);
    }



    // Getters
    public Game getUpdatedGame() {
        changedData.remove(NetworkedDataTypes.GAME);
        return game;
    }
    public Map<Long, Player> getUpdatedPlayerMap() {
        changedData.remove(NetworkedDataTypes.PLAYERS);
        return playerMap;
    }
    public UpgradeShop getUpdatedUpgradeShop() {
        changedData.remove(NetworkedDataTypes.UPGRADE_SHOP);
        return upgradeShop;
    }
    public List<Register> getUpdatedRegisters() {
        changedData.remove(NetworkedDataTypes.REGISTERS);
        return registers;
    }
    public List<Choice> getUpdatedChoices() {
        changedData.remove(NetworkedDataTypes.CHOICES);
        return choices;
    }



    /**
     * Method for manually leaving the server and game.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void disconnectFromServer(String infoMessage, int showMessageTimeInMillis) {
        if (isConnectedToServer()) {
            stopUpdateLoops();
            serverCommunication.deletePlayer(localPlayer);
            if (infoMessage == null || infoMessage.isEmpty()) infoMessage = "Disconnected from server.";
            AppController.setInfoText(infoMessage);
            runActionAndCallback(new ActionWithDelay(() -> { }, showMessageTimeInMillis, infoMessage), () -> AppController.setInfoText(""));
        }
        notifyChange();
    }



    @Override
    public void update(Subject subject) {
        if (subject.equals(serverCommunication)) {
            // If the connection to the server has timed out.
            if (serverCommunication.getHasTimedOut()) {
                disconnectFromServer("Connection to server timed out.", 2000);
            }
        }
    }

    public boolean isConnectedToServer() {
        return serverCommunication.isConnectedToServer();
    }
}
