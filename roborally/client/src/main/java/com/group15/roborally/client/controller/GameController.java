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

import com.group15.observer.Observer;
import com.group15.observer.Subject;
import com.group15.roborally.client.exceptions.UnhandledPhaseInteractionException;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.boardelements.*;
import com.group15.roborally.client.model.networking.ServerDataManager;
import com.group15.roborally.client.model.player_interaction.*;
import com.group15.roborally.client.model.upgrade_cards.*;
import com.group15.roborally.client.utils.NetworkedDataTypes;
import com.group15.roborally.client.view.BoardView;
import com.group15.roborally.server.model.Game;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.group15.roborally.client.model.CardField.CardFieldTypes.*;
import com.group15.roborally.server.model.GamePhase;
import static com.group15.roborally.server.model.GamePhase.*;
import static com.group15.roborally.client.ApplicationSettings.*;
import static com.group15.roborally.client.BoardOptions.*;

/**
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class GameController implements Observer {
    public final Board board;
    @Getter
    private final Player localPlayer;
    private final ServerDataManager serverDataManager;

    @Getter
    private Space directionOptionsSpace;

    // Player interaction
    private final Queue<PlayerInteraction> playerInteractionQueue = new LinkedList<>();
    @Getter
    private PlayerInteraction currentPlayerInteraction = null;

    private int turnCounter;
    private int movementCounter;
    @Getter
    private Player playerUpgrading;

    // Latest data
    private Game latestGameData;
    private HashMap<Long, com.group15.roborally.server.model.Player> latestPlayerData;
    private String[] latestUpgradeShopData;

    /**
     * Constructor method for GameController.
     * @param board The current board
     */
    public GameController(@NotNull Board board, Player localPlayer, ServerDataManager serverDataManager) {
        this.board = board;
        this.localPlayer = localPlayer;
        this.serverDataManager = serverDataManager;
        this.serverDataManager.attach(this);
        this.turnCounter = 0;
        this.movementCounter = 0;
        latestGameData = serverDataManager.getUpdatedGame();
        latestPlayerData = serverDataManager.getUpdatedPlayerMap();
        latestUpgradeShopData = serverDataManager.getUpdatedUpgradeShop();
    }

    /**
     * Method for starting the upgrade phase.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void startUpgradingPhase() {
        updateCurrentPhase(UPGRADE);
        board.updatePriorityList();
        if (serverDataManager.isHost()) {
            board.getUpgradeShop().refillAvailableCards();
            serverDataManager.setUpgradeShop(board.getUpgradeShop().getAvailableCardsFields());
        }

        board.updateBoard();
    }

    /**
     * Method for starting the programming phase. This is needed for resetting some parameters in order to prepare for the programming phase.
     */
    public void startProgrammingPhase() {
        updateCurrentPhase(PROGRAMMING);
        serverDataManager.setIsReady(0);

        board.setCurrentRegister(0);
        board.updatePriorityList();
        board.setCurrentPlayer(board.getPriorityList().peek());

        for (int i = 0; i < NO_OF_PLAYERS; i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                if (KEEP_HAND) {
                    player.discardProgram();
                } else {
                    player.discardAll();
                }
                for (int j = 0; j < Player.NO_OF_REGISTERS; j++) {
                    CardField field = player.getProgramField(j);
                    field.setVisible(true);
                }
                player.drawHand();
                for (int j = 0; j < NO_OF_CARDS_IN_HAND; j++) {
                    CardField field = player.getCardField(j);
                    field.setVisible(true);
                }
            }
        }
    }

    /**
     * Method for when the local player is done choosing their program and has pressed "ready".
     */
    public void finishedProgramming() {
        if (ServerDataManager.getLocalPlayer().getIsReady() == 0) {
            serverDataManager.setIsReady(1);
            if (DRAW_ON_EMPTY_REGISTER) {
                localPlayer.fillRestOfRegisters();
            }
            turnCounter++;
            serverDataManager.setPlayerRegister(localPlayer.getPlayerId(), localPlayer.getProgramFieldNames(), turnCounter);
            board.updateBoard();
            serverDataManager.updateRegisters(this::startPlayerActivationPhase);
        }
    }

    private void startPlayerActivationPhase() {
        movementCounter = 0;
        serverDataManager.setIsReady(0);
        updateCurrentPhase(PLAYER_ACTIVATION);

        for (Player player : board.getPlayers()) {
            if (player.equals(localPlayer)) {
                continue;
            }
            String[] registers = serverDataManager.getRegistersFromPlayer(player.getPlayerId());
            player.setRegisters(registers); // Convert String to CardField
        }

        board.setStepMode(false);
        handlePlayerRegister();
    }

    /**
     * Makes all players program fields, up to a certain register, visible.
     * @param register The register to set players program fields visible up to.
     */
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_OF_REGISTERS) {
            for (int i = 0; i < NO_OF_PLAYERS; i++) {
                Player player = board.getPlayer(i);
                CardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    /*
     *    ### These methods are the main flow of the activation phases. ###
     */
    /**
     * Gets the next player in the priority queue, queues that player's command card and executes it.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void handlePlayerRegister() {
        System.out.println();
        board.setCurrentPlayer(board.getPriorityList().poll());
        makeProgramFieldsVisible(board.getCurrentRegister());
        Player currentPlayer = board.getCurrentPlayer();
        if (!currentPlayer.getIsRebooting()) {
            // Handle the players command on the current register. This will queue any command on the register.
            queuePlayerCommandFromCommandCard(currentPlayer);
        }
        // Begin handling the actions.
        handlePlayerActions();
    }

    /**
     * This method splits up handlePlayerRegister(), in order to call this again, if the action queue was interrupted by a PlayerInteraction.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void handlePlayerActions() {
        // Run through the queue and execute the player command.
        runActionsAndCallback(this::handleEndOfPlayerTurn, board.getBoardActionQueue());
    }

    /**
     * Handles the end of the current player's register execution.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void handleEndOfPlayerTurn() {
        if (getIsPlayerInteracting()) { // Return and wait for player interaction.
            return;
        }
        serverDataManager.setChoices();
        serverDataManager.updateChoices(this::executeUpgradeCards, movementCounter, turnCounter);
    }

    public void useUpgradeCard(UpgradeCard card){
        serverDataManager.addUsedUpgradeCard(card.getEnum().name(), movementCounter, turnCounter);
    }

    private void executeUpgradeCards(){
        for(Player player : board.getPlayers()){
            List<String> usedCards = serverDataManager.getUsedUpgrades(player.getName());
            for(String card : usedCards){
                for(CardField field : player.getPermanentUpgradeCardFields()){
                    if(field.getCard() != null && ((UpgradeCard) field.getCard()).getEnum().name().equals(card)){
                        ((UpgradeCard) field.getCard()).onActivated();
                    }
                }
                for(CardField field : player.getTemporaryUpgradeCardFields()){
                    if(field.getCard() != null && ((UpgradeCard) field.getCard()).getEnum().name().equals(card)){
                        ((UpgradeCard) field.getCard()).onActivated();
                    }
                }
            }
        }
        nextMovement();
    }

    private void nextMovement(){
        movementCounter++;
        if (!board.getPriorityList().isEmpty()) {
            handlePlayerRegister(); // There are more players in the priorityList. Continue to next player.
        } else {
            startBoardActivationPhase(); // PriorityList is empty, therefore we end the register.
        }
    }

    /**
     * Handles the end of a register.
     * Here we queue board elements and player lasers, then execute them.
     * Afterwards, we set the next register, calling handleNextPlayerTurn() again.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void startBoardActivationPhase() {
        updateCurrentPhase(BOARD_ACTIVATION);
        // Queue board elements and player lasers.
        queueBoardElementsAndRobotLasers();
        handleBoardElementActions();
    }

    /**
     * This method splits up handleEndOfRegister(), in order to call this again, if the action queue was interrupted by a PlayerInteraction.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void handleBoardElementActions() {
        // Execute board elements and player lasers. When actions have taken place, we go to the next register.
        runActionsAndCallback(this::nextRegister, board.getBoardActionQueue());
    }

    /**
     * Continues to next register, or ends the round if it was the last register.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void nextRegister() {
        if (getIsPlayerInteracting()) {
            // Return and wait for player interaction.
            return;
        }
        int currentRegister = board.getCurrentRegister();
        if (currentRegister < Player.NO_OF_REGISTERS - 1) {
            // Set next register
            currentRegister++;
            // If there are more registers, set the currentRegister and continue to the next player.
            board.setCurrentRegister(currentRegister);
            board.updatePriorityList();
            board.updateBoard();
            if (!board.isStepMode()) {
                handlePlayerRegister();
            }
        } else {
            handleEndOfRound();
        }
    }

    /**
     * Handles what happens when a round is done.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void handleEndOfRound() {
        // If all registers are done
        PauseTransition pause = new PauseTransition(Duration.millis(NEXT_REGISTER_DELAY));
        pause.setOnFinished(_ -> {
            for (Player player : board.getPlayers()) {
                player.stopRebooting();
                player.getSpace().updateSpace();
            }
            startUpgradingPhase();
        });  // Small delay before ending activation phase for dramatic effect ;-).
        pause.play();
    }

    /**
     * This method exhausts the action queue by removing the first action, executing it, waits for the action delay, then calls itself again.
     * Is interrupted if the action queue is empty or if there is a player action.
     * @param callback The method to call back to, when the action queue is exhausted.
     * @param actionQueue The action queue to execute.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void runActionsAndCallback(Runnable callback, LinkedList<ActionWithDelay> actionQueue) {
        if (playerInteractionQueue.isEmpty()) {
            if (board.getCurrentPhase() == PLAYER_ACTIVATION || board.getCurrentPhase() == BOARD_ACTIVATION) {
                if (!actionQueue.isEmpty()) { // As long as there are more actions.
                    // Handle the next action
                    ActionWithDelay nextAction = actionQueue.removeFirst();
                    nextAction.getAction(DEBUG_WITH_ACTION_MESSAGE).run();
                    int delayInMillis = nextAction.getDelayInMillis();
                    PauseTransition pause = new PauseTransition(Duration.millis(delayInMillis));
                    pause.setOnFinished(_ -> {
                        EventHandler.event_EndOfAction(this);
                        runActionsAndCallback(callback, actionQueue);
                    }); // Continue actions
                    if (WITH_ACTION_DELAY) {
                        pause.play();
                    }
                } else { // When we have exhausted the actions, call the callback method.
                    callback.run();
                }
            } else {
                System.out.println("Possible error? GamePhase is: \"" + board.getCurrentPhase() + "\", but currently running actions.");
            }
        } else {
            handleNextInteraction();
        }
    }
    /*
            ### This concludes the "main flow" of the activation phase. ###
     */


    /**
     * Method for making a new player interaction. This stops the action queue execution loop and begins the interaction.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void addPlayerInteraction(PlayerInteraction interaction) {
        playerInteractionQueue.offer(interaction);
        board.updateBoard();
    }

    /**
     * @return whether there is an ongoing player interaction or not.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public boolean getIsPlayerInteracting() {
        return currentPlayerInteraction != null || !playerInteractionQueue.isEmpty();
    }

    /**
     * Polls the first player interaction in the queue and initializes it.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void handleNextInteraction() {
        // Check if there are more interactions.
        if (playerInteractionQueue.isEmpty()) {
            // If not, continue
            try {
                continueActions();
            } catch (UnhandledPhaseInteractionException e) {
                System.out.println(e.getMessage());
            }
        } else {
            currentPlayerInteraction = playerInteractionQueue.poll();
            currentPlayerInteraction.initializeInteraction();
            board.updateBoard();
        }
    }

    /**
     * Handles what method to go to, after the player interaction queue is empty.
     * @throws UnhandledPhaseInteractionException If it is not specified what method to go to after player interactions at the current phase.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void continueActions() throws UnhandledPhaseInteractionException {
        if (board.getCurrentPhase() == PLAYER_ACTIVATION) {
            currentPlayerInteraction = null;
            handlePlayerActions();
        } else if (board.getCurrentPhase() == BOARD_ACTIVATION) {
            currentPlayerInteraction = null;
            handleBoardElementActions();
        } else if (board.getCurrentPhase() == PROGRAMMING) {
            currentPlayerInteraction = null;
            board.getCurrentPlayer().stopRebooting();
        } else {
            throw new UnhandledPhaseInteractionException(board.getCurrentPhase(), currentPlayerInteraction);
        }
    }

    /**
     * Handles the player command for a command with multiple options
     *  @author Michael Sylvest Bendtsen, s214954@dtu.dk
     *  @param option the option the player have chosen, and sets the activation phase active again
     */
    public void executeCommandOptionAndContinue(Command option) {
        currentPlayerInteraction.getPlayer().queueCommand(option, false, this);
        currentPlayerInteraction.interactionFinished();
    }

    /**
     * Gets the command card in the players program at the current register.
     * @param currentPlayer The player, whose command card should be queued.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void queuePlayerCommandFromCommandCard(Player currentPlayer) {
        Command commandToQueue = null;
        try {
            int currentRegister = board.getCurrentRegister();
            CommandCard card = (CommandCard) currentPlayer.getProgramField(currentRegister).getCard();
            if (card == null) return;
            commandToQueue = card.command;
            currentPlayer.queueCommand(commandToQueue, this);
        } catch (Exception e) {
            System.out.println("ERROR - Something went wrong when trying to get command: \"" + commandToQueue + "\" from CommandCard.");
            System.out.println(e.getMessage());
        }
        assert false;
    }

    /**
     * Queues all the board elements in the board.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void queueBoardElementsAndRobotLasers() {
        board.queueBoardElementsWithIndex(this, 0, "Blue conveyor belts");
        board.queueBoardElementsWithIndex(this, 1, "Green conveyor belts");
        board.queueBoardElementsWithIndex(this, 2, "Push panels");
        board.queueBoardElementsWithIndex(this, 3, "Gears");
        board.queueBoardElementsWithIndex(this, 4, "Board lasers");
        board.queueClearLasers();
        board.queuePlayerLasers();
        board.queueClearLasers();
        board.queueBoardElementsWithIndex(this, 5, "Energy spaces");
        board.queueBoardElementsWithIndex(this, 6, "Checkpoints");
    }

    /**
     * Checks whether a player has reached the last checkpoint.
     * @param player The player who reached a checkpoint.
     * @param number The checkpoint number.
     */
    public void checkpointReached(Player player, int number) {
        if(number >= board.getNumberOfCheckpoints()){
            setGameOver(player);
        }
    }

    /**
     * Calls gameOver() from the AppController.
     * @param winner The player who won.
     * @author Maximillian Bjørn Mortensen
     */
    public void setGameOver(Player winner) {
        AppController.gameOver(winner);
    }

    public boolean getIsLocalPlayerReady() {
        return ServerDataManager.getLocalPlayer().getIsReady() == 1;
    }

    /**
     * Method for checking if a card is allowed to be dragged.
     * @param sourceField The CardField to drag.
     * @return Whether the card can be dragged.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public boolean canDragCard(CardField sourceField) {
        if (sourceField == null) return false;
        if (sourceField.getCard() == null) return false;
        CardField.CardFieldTypes sourceType = sourceField.cardFieldType;

        // Drag from shop
        if (sourceType == UPGRADE_CARD_SHOP_FIELD && board.getCurrentPhase() != UPGRADE) return false;

        // Drag from player
        if (sourceField.cardFieldType != UPGRADE_CARD_SHOP_FIELD) {
            // Limited card movement when not programming
            assert sourceField.player != null;
            List<CardField> playerProgramField = Arrays.stream(sourceField.player.getProgramFields()).toList();
            if (board.getCurrentPhase() != PROGRAMMING) {
                return !playerProgramField.contains(sourceField);
            }
        }

        return true;
    }

    /**
     * Method for checking if the currently dragged card is allowed to be dropped at the hovered CardField.
     * @param sourceField The CardField being dragged from.
     * @param targetField The hovered CardField.
     * @return Whether the card can be dropped at the hovered CardField.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public boolean canDropCard(CardField sourceField, CardField targetField) {
        if (sourceField == null || targetField == null) return false;
        CardField.CardFieldTypes sourceType = sourceField.cardFieldType;
        CardField.CardFieldTypes targetType = targetField.cardFieldType;

        // Can't drag onto shop
        if (targetType == UPGRADE_CARD_SHOP_FIELD) return false;

        // Dragging from shop
        if (sourceType == UPGRADE_CARD_SHOP_FIELD) {
            if (board.getCurrentPhase() != UPGRADE) return false;
            if (sourceField.getCard() instanceof UpgradeCardPermanent && targetType != PERMANENT_UPGRADE_CARD_FIELD) return false;
            if (sourceField.getCard() instanceof UpgradeCardTemporary && targetType != TEMPORARY_UPGRADE_CARD_FIELD) return false;
        }

        // Dragging from player
        if (sourceField.cardFieldType != UPGRADE_CARD_SHOP_FIELD) {
            if (sourceField.player != targetField.player) return false;
            if (sourceType != targetType) return false;

            // Limited card movement when not programming
            assert sourceField.player != null;
            List<CardField> playerProgramField = Arrays.stream(sourceField.player.getProgramFields()).toList();
            if (board.getCurrentPhase() != PROGRAMMING) {
                if (playerProgramField.contains(sourceField) || playerProgramField.contains(targetField)) return false;
            }

            // Can't put again command on first register
            if (sourceField.cardFieldType == COMMAND_CARD_FIELD) {
                return ((CommandCard) (sourceField.getCard())).command != Command.AGAIN || targetField.index != 1;
            }
        }

        return true;
    }

    /**
     * Method for moving a card from one CardField to another.
     *
     * @param sourceField The CardField being moved from.
     * @param targetField The CardField being moved to.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void moveCard(@NotNull CardField sourceField, @NotNull CardField targetField) {
        boolean couldMove = true;
        Card sourceCard = sourceField.getCard();
        Card targetCard = targetField.getCard();

        // Buying
        if (sourceField.cardFieldType == UPGRADE_CARD_SHOP_FIELD) {
            assert targetField.player != null;
            Player player = targetField.player;
            boolean canBuyUpgradeCard = player.attemptUpgradeCardPurchase(sourceField.getCard(), this);
            if (canBuyUpgradeCard) {
                if (targetCard != null) {
                    player.removeUpgradeCard((UpgradeCard) targetCard);
                }
            }
            couldMove = canBuyUpgradeCard;
        }

        if (couldMove) {
            if (sourceField.cardFieldType != UPGRADE_CARD_SHOP_FIELD) {
                sourceField.setCard(targetCard); // Replaces sourceField card with null if targetCard is null.
            } else {
                sourceField.setCard(null);
            }
            targetField.setCard(sourceCard);

            // If the player just bought an upgrade card
            if (sourceField.cardFieldType == UPGRADE_CARD_SHOP_FIELD) {
                if (targetField.cardFieldType == PERMANENT_UPGRADE_CARD_FIELD || targetField.cardFieldType == TEMPORARY_UPGRADE_CARD_FIELD) {
                    updatePlayerCards();
                }
            }
        }
    }

    /**
     * Method for setting the direction pane position at a space.
     * @param space The space that the direction pane should appear at.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setDirectionOptionsPane(Player player, Space space) {
        if (player != null && player.equals(localPlayer)) {
            if (space != null) {
                space.updateSpace();
            }
            directionOptionsSpace = space;
        } else {
            directionOptionsSpace = null;
        }

        board.updateBoard();
    }


    // Updates to the server
    /**
     * Called from view when a space was clicked.
     * @param space The space that was clicked.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void spacePressed(Space space) {
        if (board.getCurrentPhase() == INITIALIZATION) {
            if (space.getBoardElement() instanceof BE_SpawnPoint) {
                if (space.getPlayer() == null) {
                    serverDataManager.setPlayerSpawn(space, null);
                }
            }
        }
    }

    /**
     * This method is called when the player has chosen a direction for the robot.
     * The direction is set for the current player and the next player is set as the current player.
     * If the next player has a spawn point, the programming phase is started.
     *
     * @param direction the direction chosen by the player
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void chooseDirection(Heading direction, BoardView boardView) {
        boardView.handleDirectionButtonClicked();
        directionOptionsSpace = null;
        if (board.getCurrentPhase() == INITIALIZATION) {
            localPlayer.setHeading(direction);
            serverDataManager.setPlayerSpawn(localPlayer.getSpace(), direction.name());
            serverDataManager.setIsReady(1);
        } else {
            currentPlayerInteraction.getPlayer().setHeading(direction);
            currentPlayerInteraction.interactionFinished();
        }
    }

    /**
     * Updates the GamePhase locally, and tells the server to change GamePhase if this is the host.
     * @param phase The GamePhase to switch to.
     */
    public void updateCurrentPhase(GamePhase phase) {
        if (serverDataManager.isHost()) {
            serverDataManager.updatePhase(phase);
        }
        board.setCurrentPhase(phase);
    }

    /**
     * Called when the player either buys an Upgrade Card, or clicks the "Finish Upgrading" button.
     * Tells the server to update the player cards and that the player is ready.
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     */
    public void updatePlayerCards() {
        String[] permCards = new String[Player.NO_OF_PERMANENT_UPGRADE_CARDS];
        String[] tempCards = new String[Player.NO_OF_TEMPORARY_UPGRADE_CARDS];

        for (int i = 0; i < Player.NO_OF_PERMANENT_UPGRADE_CARDS; i++) {
            UpgradeCard card = (UpgradeCard) localPlayer.getPermanentUpgradeCardField(i).getCard();
            if (card != null)
                permCards[i] = card.getEnum().name();
        }
        for (int i = 0; i < Player.NO_OF_TEMPORARY_UPGRADE_CARDS; i++) {
            UpgradeCard card = (UpgradeCard) localPlayer.getTemporaryUpgradeCardField(i).getCard();
            if (card != null)
                tempCards[i] = card.getEnum().name();
        }

        serverDataManager.setPlayerUpgradeCards(permCards, tempCards);
        serverDataManager.setIsReady(1);
    }

    // Updates from server
    @Override
    public void update(Subject subject) {
        if (subject.equals(serverDataManager)) {
            // Updating data
            List<NetworkedDataTypes> changedData = ServerDataManager.getChangedData();

            System.out.println();
            System.out.println("Changed data: " + changedData);

            if (changedData.contains(NetworkedDataTypes.GAME)) {
                latestGameData = serverDataManager.getUpdatedGame();
            }
            if (changedData.contains(NetworkedDataTypes.PLAYERS)) {
                latestPlayerData = serverDataManager.getUpdatedPlayerMap();
            }
            if (changedData.contains(NetworkedDataTypes.GAME)) {
                latestUpgradeShopData = serverDataManager.getUpdatedUpgradeShop();
            }

            if (latestGameData == null || latestPlayerData == null) return;

            System.out.println("GamePhase: " + latestGameData.getPhase());

            // Check if any player disconnected
            for (Player client : board.getPlayers()) {
                com.group15.roborally.server.model.Player updatedPlayer = latestPlayerData.get(client.getPlayerId());
                if (updatedPlayer == null) {
                    // Player disconnected.
                    // TODO: Instead of disconnecting this player, the disconnected player should be removed from the game.
                    serverDataManager.disconnectFromServer("Player: \"" + client.getName() + "\" disconnected from the game.", 2000);
                }
            }

            // Update logic for the current GamePhase locally from the data received from the server.
            switch (board.getCurrentPhase()) {
                case INITIALIZATION -> updateInitialization();
                case PROGRAMMING -> updateProgramming();
                case UPGRADE -> {
                    if (latestUpgradeShopData != null) {
                        updateUpgrading();
                    } else {
                        System.out.println("Shop is null");
                    }
                }
            }
        }
    }

    /**
     *
     */
    private void updateInitialization() {
        // Check if all players have set their spawn point
        boolean allHaveSetSpawnPoint = true;

        for (Player client : board.getPlayers()) {
            com.group15.roborally.server.model.Player updatedPlayer = latestPlayerData.get(client.getPlayerId());
            if (updatedPlayer == null) continue; // Player disconnected.
            if (client.getSpawnPoint() != null) continue; // Client already has a spawn point
            int[] clientSpawnPoint = updatedPlayer.getSpawnPoint();
            if (clientSpawnPoint == null) {
                // Client hasn't chosen a spawn point.
                allHaveSetSpawnPoint = false;
                continue;
            }
            Space clientSpawnPosition = board.getSpace(clientSpawnPoint[0], clientSpawnPoint[1]);
            if (clientSpawnPosition == null) continue; // Can't find space at position.

            // Local player direction options
            if (client.equals(localPlayer)) {
                if (ServerDataManager.getLocalPlayer().getIsReady() == 0 && board.getCurrentPhase() == INITIALIZATION) {
                    // Local player direction option
                    setDirectionOptionsPane(client, clientSpawnPosition);
                } else {
                    setDirectionOptionsPane(null, null);
                }
            }

            // Setting player at selected position
            client.setSpace(clientSpawnPosition);
            board.updateBoard();

            // Heading
            String playerSpawnDirection = updatedPlayer.getSpawnDirection();
            if (playerSpawnDirection == null || playerSpawnDirection.isBlank()) {
                // Client hasn't chosen a direction.
                allHaveSetSpawnPoint = false;
                continue;
            }

            // Setting spawnPoint
            Heading clientHeading = Heading.valueOf(playerSpawnDirection);
            client.setHeading(clientHeading);
            client.setSpawn(clientSpawnPosition);
            System.out.println("Setting spawn point for " + client.getName());
            if (clientSpawnPosition.getBoardElement() instanceof BE_SpawnPoint spawnPoint) {
                spawnPoint.setColor(client);
                board.updateBoard();
            }
        }

        if (allHaveSetSpawnPoint) {
            startProgrammingPhase();
            setDirectionOptionsPane(null, null);
        }
    }

    private void updateProgramming() {

    }

    /**
     * Updates the players' upgrade cards from the server.
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void updateUpgrading() {
        System.out.println("Updating upgrade shop");
        // Updating players upgrade cards.
        for (Player client : board.getPlayers()) {
            com.group15.roborally.server.model.Player updatedPlayer = latestPlayerData.get(client.getPlayerId());
            if (updatedPlayer == null)
                continue;

            String[] permCardsStr = updatedPlayer.getPermCards();
            String[] tempCardsStr = updatedPlayer.getTempCards();

            if (permCardsStr != null) {
                for (int i = 0; i < Player.NO_OF_PERMANENT_UPGRADE_CARDS; i++) {
                    if (permCardsStr[i] != null) {
                        UpgradeCard upgradeCard = UpgradeCard.getUpgradeCardFromClass(UpgradeCards.valueOf(permCardsStr[i]).upgradeCardClass);
                        client.attemptUpgradeCardPurchase(upgradeCard, this);
                        client.getPermanentUpgradeCardField(i).setCard(upgradeCard);
                    }
                }
            }
            if (tempCardsStr != null) {
                for (int i = 0; i < Player.NO_OF_TEMPORARY_UPGRADE_CARDS; i++) {
                    if (tempCardsStr[i] != null) {
                        UpgradeCard upgradeCard = UpgradeCard.getUpgradeCardFromClass(UpgradeCards.valueOf(tempCardsStr[i]).upgradeCardClass);
                        client.attemptUpgradeCardPurchase(upgradeCard, this);
                        client.getTemporaryUpgradeCardField(i).setCard(upgradeCard);
                    }
                }
            }
        }

        int upgradeTurn = 0;
        for (int i = 0; i < board.getPriorityList().size(); i++) {
            Player client = board.getPriorityList().stream().toList().get(i);
            com.group15.roborally.server.model.Player updatedPlayer = latestPlayerData.get(client.getPlayerId());
            if (updatedPlayer.getIsReady() == 1) {
                upgradeTurn++;
            } else {
                break;
            }
        }

        System.out.println("upgrade turn: " + upgradeTurn);

        board.getUpgradeShop().setAvailableCards(latestUpgradeShopData);

        // Finish check
        if (upgradeTurn >= NO_OF_PLAYERS) {
            startProgrammingPhase();
        }

        // Set turn
        playerUpgrading = board.getPriorityList().stream().toList().get(upgradeTurn);
        System.out.println("Player upgrading: " + playerUpgrading.getName());
        board.updateBoard();
    }
}
