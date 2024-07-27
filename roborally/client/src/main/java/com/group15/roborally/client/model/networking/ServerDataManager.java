package com.group15.roborally.client.model.networking;

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
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.group15.roborally.client.BoardOptions.NO_OF_PLAYERS;

/**
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class ServerDataManager extends Subject implements Observer {
    private final ServerCommunication serverCommunication = new ServerCommunication();
    private ScheduledExecutorService gameUpdateScheduler;
    private ScheduledExecutorService serverPoller;

    private final Random random = new Random();

    @Getter
    private Player localPlayer;
    @Getter
    private boolean isHost = false;

    @Getter
    private Interaction interaction;
    @Setter
    private int currentTurnCount, currentPhaseCount, currentMovementCount = 0;
    @Setter
    private GamePhase currentPhase = GamePhase.LOBBY;

    // Updated Game data
    private Game game;
    private HashMap<Long, Player> playerMap;
    private String[] upgradeShop;
    private List<Register> registers;
    @Getter
    private List<Choice> choices;
    @Getter
    private final static List<NetworkedDataTypes> changedData = new ArrayList<>();

    // Server queries.
    /**
     * Calls the server and requests to create a new game.
     * Adds delay in between messages to be able to read them.
     * Adds a random delay after creating the game.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void tryCreateAndJoinGame(String serverURL, String playerName) {
        AppController.setInfoText("Creating new game...");
        AtomicLong gameId = new AtomicLong();
        runActionAndCallback(new ActionWithDelay(() -> gameId.set(serverCommunication.createGame(serverURL)), random.nextInt(50, 250)), () -> {
            if (gameId.get() != -1) {
                runActionAndCallback(new ActionWithDelay(
                        () -> AppController.setInfoText("Successfully created new game!"), 250),
                        () -> tryJoinGameWithGameID(serverURL, gameId.get(), playerName));
            } else {
                runActionAndCallback(new ActionWithDelay(
                        () -> AppController.setInfoText("Failed to create new game."),1500),
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
    public void tryJoinGameWithGameID(String serverURL, long gameId, String playerName) {
        AppController.setInfoText("Joining game...");
        AtomicReference<Player> player = new AtomicReference<>();
        runActionAndCallback(new ActionWithDelay(() -> player.set(serverCommunication.joinGame(serverURL, gameId, playerName)), random.nextInt(50, 250)), () -> {
            if (player.get() != null) {
                runActionAndCallback(new ActionWithDelay(() -> {
                    AppController.setInfoText("Successfully joined game!");
                    connectedToGame(gameId, player.get());
                }, 250), () -> AppController.setInfoText(""));
            } else {
                runActionAndCallback(new ActionWithDelay(
                        () -> AppController.setInfoText("Failed to join game."), 1500),
                        () -> AppController.setInfoText(""));
            }
        });
    }


    /**
     * Initializes the multiplayer menu with the server data for the local player. Is called when the server tells the player they can join the game.
     * @param gameId The game's gameId received from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void connectedToGame(long gameId, Player localPlayer) {
        this.playerMap = new HashMap<>();
        this.upgradeShop = null;
        this.registers = null;
        this.game = serverCommunication.getGame(gameId);
        this.localPlayer = localPlayer;
        this.isHost = localPlayer.getPlayerId() == game.getHostId();
        List<Player> players = serverCommunication.getPlayers(gameId);
        serverCommunication.attach(this);
        setReadyForPhase(GamePhase.LOBBY);
        loadServerData(game, players);
        startUpdateGameLoop();
        notifyChange();
        System.out.println("Player name: " + localPlayer.getPlayerName());
    }

    /**
     * Starts a loop of fetching game data from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void startUpdateGameLoop() {
        Runnable lobbyUpdate = () -> {
            if (serverCommunication.isConnectedToServer()) {
                if (this.game == null) {
                    System.err.println("THIS.GAME IS NULL");
                    return;
                }
                try {
                    updateGameFromServerData();
                } catch (Exception e) {
                    System.err.println("Error in updateGameFromServerData loop. Exception message: " + e.getMessage());
                }
            }
        };
        gameUpdateScheduler = Executors.newScheduledThreadPool(1);
        gameUpdateScheduler.scheduleAtFixedRate(lobbyUpdate, 1, 100, TimeUnit.MILLISECONDS);
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
        }
        String finalInfoMessage = infoMessage;
        runActionAndCallback(new ActionWithDelay(() -> {
            AppController.setInfoText(finalInfoMessage);
            if (serverCommunication.isConnectedToServer()) {
                serverCommunication.deletePlayer(localPlayer);
            }
            stopGameUpdateLoop();
        }, showMessageTimeInMillis), () -> AppController.setInfoText(""));
    }

    /**
     * Stops the loop of updating the client with data from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void stopGameUpdateLoop() {
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
        List<Player> updatedPlayers = serverCommunication.getPlayers(game.getGameId());
        if (updatedPlayers == null) {
            System.err.println("PLAYER LIST IS NULL");
            return;
        }
        String[] updatedUpgradeShop;
        if (currentPhase.equals(GamePhase.UPGRADE)) {
            updatedUpgradeShop = serverCommunication.getUpgradeShop(game.getGameId());
        } else {
            updatedUpgradeShop = null;
        }
        List<Register> updatedRegisters;
        if (currentPhase.equals(GamePhase.PROGRAMMING)) {
            updatedRegisters = serverCommunication.getRegisters(game.getGameId());
        } else {
            updatedRegisters = null;
        }
        List<Choice> updatedChoices = serverCommunication.getChoices(game.getGameId(), currentPhaseCount);

        // Check if the host has disconnected
        boolean hostHasDisconnected = updatedPlayers.stream().noneMatch(player -> updatedGame.getHostId() == player.getPlayerId());
        if (hostHasDisconnected) {
            disconnectFromServer("The host left the game.", 3000);
            return;
        }

        // Update the game in the JavaFX application thread.
        Platform.runLater(() -> loadServerDataAndNotify(updatedGame, updatedPlayers, updatedUpgradeShop, updatedRegisters, updatedChoices));
    }

    /**
     * Loads the data received from the server and notifies listeners if any data has changed.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void loadServerDataAndNotify(Game updatedGame, List<Player> updatedPlayers, String[] updatedUpgradeShop, List<Register> updatedRegisters, List<Choice> updatedChoices) {
        boolean hasChanges = false;
        // Update any data that has changed.
        if (updatedGame.hasChanges(this.game)) {
            updateGameData(updatedGame);
            hasChanges = true;
        }
        if (this.game.getNrOfPlayers() != updatedGame.getNrOfPlayers() ||
                updatedPlayers.stream().anyMatch(updatedPlayer -> updatedPlayer.hasChanges(playerMap.get(updatedPlayer.getPlayerId())))) {
            updatePlayerData(updatedPlayers);
            hasChanges = true;
        }
        if (updatedUpgradeShop != null) {
            if (!Arrays.equals(updatedUpgradeShop, this.upgradeShop)) {
                updateUpgradeShopData(updatedUpgradeShop);
                hasChanges = true;
            }
        }
        if (updatedRegisters != null) {
            if (this.registers == null ||
                    this.registers.size() != updatedRegisters.size() ||
                    IntStream.range(0, updatedRegisters.size()).anyMatch(i -> updatedRegisters.get(i).hasChanges(this.registers.get(i)))) {
                updateRegisterData(updatedRegisters);
                hasChanges = true;
            }
        }
        if (updatedChoices != null) {
            if (!Choice.areListsEqual(this.choices, updatedChoices)) {
                updateChoiceData(updatedChoices);
                hasChanges = true;
            }
        }
        if (hasChanges) {
            notifyChange();
        }
    }
    private void loadServerData(Game updatedGame, List<Player> updatedPlayers) {
        updateGameData(updatedGame);
        updatePlayerData(updatedPlayers);
    }

    private void updateGameData(@NotNull Game updatedGameData) {
        this.game = updatedGameData;
        NO_OF_PLAYERS = updatedGameData.getNrOfPlayers();
        changedData.add(NetworkedDataTypes.GAME);
    }

    private void updatePlayerData(@NotNull List<Player> updatedPlayerData) {
        Map<Long, Player> updatedPlayerMap = updatedPlayerData.stream()
                .collect(Collectors.toMap(Player::getPlayerId, player -> player));
        this.playerMap.clear();
        this.playerMap.putAll(updatedPlayerMap);
        changedData.add(NetworkedDataTypes.PLAYERS);
    }

    private void updateUpgradeShopData(@NotNull String[] updatedUpgradeShop) {
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
        actionWithDelay.getAction(false).run();
        PauseTransition pause = new PauseTransition(Duration.millis(actionWithDelay.getDelayInMillis()));
        pause.setOnFinished(a -> callback.run());
        pause.play();
    }

    /**
     * Method for when the connection to the server was lost, and the reconnection timed out.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void connectionToServerTimedOut() {
        runActionAndCallback(new ActionWithDelay(() -> {
            serverCommunication.detach(this);
            AppController.setInfoText("Connection to server timed out.");
            notifyChange();
            stopGameUpdateLoop();
        }, 2000), () -> AppController.setInfoText(""));
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
            serverCommunication.putPlayer(localPlayer);
        }
    }
    public void changeCourse(@NotNull CC_CourseData selectedCourse) {
        if (!selectedCourse.getCourseName().equals(game.getCourseName())) {
            game.setCourseName(selectedCourse.getCourseName());
            serverCommunication.putGame(game);
        }
    }
    public void setGamePhase(@NotNull GamePhase gamePhase) {
        if (isHost && !gamePhase.equals(game.getPhase())) {
            game.setPhase(gamePhase);
            serverCommunication.putGame(game);
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
    public void setUpgradeShop(@NotNull String[] availableCards) {
        if (!Arrays.equals(upgradeShop, availableCards)) {
            serverCommunication.putUpgradeShop(availableCards, game.getGameId());
        }
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
        ChoiceDTO emptyChoiceDTO = new ChoiceDTO(game.getGameId(), localPlayer.getPlayerId(), Choice.READY_CHOICE, currentPhaseCount, Choice.ResolveStatus.NONE.name());
        serverCommunication.putChoice(emptyChoiceDTO);
    }
    public void waitForChoicesAndCallback(Runnable callback) {
        Runnable poll = () -> {
            List<Choice> choices = serverCommunication.getChoices(game.getGameId(), currentPhaseCount);
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
        InteractionDTO interactionDTO = new InteractionDTO(localPlayer.getPlayerId(), interaction, currentTurnCount, currentMovementCount);
        serverCommunication.putInteraction(interactionDTO);
    }
    public void waitForInteractionAndCallback(Runnable callback, long playerId, int turn, int movement) {
        interaction = null;
        Runnable poll = () -> {
            interaction = serverCommunication.getInteraction(playerId, turn, movement);
            if (interaction != null) {
                serverPoller.shutdownNow();
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
    public HashMap<Long, Player> getUpdatedPlayerMap() {
        changedData.remove(NetworkedDataTypes.PLAYERS);
        return playerMap;
    }
    public String[] getUpdatedUpgradeShop() {
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



    @Override
    public void update(Subject subject) {
        if (subject.equals(serverCommunication)) {
            // If the player was disconnected from the server.
            if (!isConnectedToServer()) {
                connectionToServerTimedOut();
            }
        }
    }

    public boolean isConnectedToServer() {
        return serverCommunication.isConnectedToServer();
    }
}
