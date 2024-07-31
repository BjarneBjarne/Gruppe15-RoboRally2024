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

import com.group15.roborally.client.ApplicationSettings;
import com.group15.roborally.client.RoboRally;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.view.GameView;
import com.group15.roborally.client.exceptions.UnhandledPhaseInteractionException;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.boardelements.*;
import com.group15.roborally.client.model.networking.ServerDataManager;
import com.group15.roborally.client.model.player_interaction.*;
import com.group15.roborally.client.model.upgrade_cards.*;
import com.group15.roborally.client.utils.NetworkedDataTypes;
import com.group15.roborally.common.model.*;
import com.group15.roborally.common.model.UpgradeShop;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.group15.roborally.client.model.CardField.CardFieldTypes.*;
import static com.group15.roborally.client.ApplicationSettings.*;
import static com.group15.roborally.client.LobbySettings.*;

/**
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class GameController {
    public final Board board;
    @Getter
    private final Player localPlayer;
    @Getter
    private final ServerDataManager serverDataManager;
    @Getter
    private Space directionOptionsSpace;
    @Getter
    private int turnCounter, phaseCounter, waitCounter, interactionCounter = 0;
    @Getter
    private Player playerUpgrading;
    @Getter
    private boolean handlingPrePhase = false;
    private boolean waitingForCardUse = false;

    // Player interaction
    private final Queue<PlayerInteraction> playerInteractionQueue = new LinkedList<>();
    @Getter
    private PlayerInteraction currentPlayerInteraction = null;

    // Choices
    private final List<Choice> executedChoices = new ArrayList<>();
    @Getter
    private final List<ChoiceDTO> unresolvedLocalChoices = new ArrayList<>();

    // Latest data
    private Game latestGameData;
    private Map<Long, com.group15.roborally.common.model.Player> latestPlayerData;
    @Getter
    private UpgradeShop latestUpgradeShopData;
    private List<Register> latestRegisterData;
    private List<Choice> latestChoiceData;
    private AnimationTimer countdownTimer = null;
    private int countdownTimeLeft = -1;

    /**
     * Constructor method for GameController.
     * @param board The current board
     */
    public GameController(@NotNull Board board, Player localPlayer, ServerDataManager serverDataManager) {
        this.board = board;
        this.localPlayer = localPlayer;
        this.serverDataManager = serverDataManager;
        latestGameData = serverDataManager.getUpdatedGame();
        latestPlayerData = serverDataManager.getUpdatedPlayerMap();
        latestUpgradeShopData = serverDataManager.getUpdatedUpgradeShop();
        latestRegisterData = serverDataManager.getUpdatedRegisters();
        board.setStepMode(false);

        //localPlayer.tryAddFreeUpgradeCard(UpgradeCard.getUpgradeCardFromClass(Card_SpamBlocker.class), this, 0);
    }

    private void incrementTurnCounter() {
        turnCounter++;
        waitCounter = 0;
        interactionCounter = 0;
        serverDataManager.setCurrentTurnCount(turnCounter);
        serverDataManager.setCurrentWaitCount(waitCounter);
        updateDebuggingOfCounters();
    }
    private void incrementPhaseCounter() {
        phaseCounter++;
        serverDataManager.setCurrentPhaseCount(phaseCounter);
        updateDebuggingOfCounters();
    }
    private void incrementWaitCounter() {
        waitCounter++;
        serverDataManager.setCurrentWaitCount(waitCounter);
        updateDebuggingOfCounters();
    }
    private void incrementInteractionCounter() {
        interactionCounter++;
        serverDataManager.setCurrentInteractionCount(interactionCounter);
        updateDebuggingOfCounters();
    }


    private void updateDebuggingOfCounters() {
        RoboRally.setDebugText(4, "Turn counter: " + turnCounter + ", wait counter: " + waitCounter + ", interaction counter: " + interactionCounter + ", phase counter: " + phaseCounter);
    }

    /**
     * Method for starting the upgrade phase.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void startUpgradingPhase() {
        board.updatePriorityList();
        // The host sets the available upgrade cards and sends it to the server.
        if (serverDataManager.isHost()) {
            board.getUpgradeShop().refillAvailableCards();
            CardField[] availableCardsFields = board.getUpgradeShop().getAvailableCardsFields();
            String[] availableCards = new String[availableCardsFields.length];
            for (int i = 0; i < availableCards.length; i++) {
                if (availableCardsFields[i].getCard() == null) {
                    availableCards[i] = null;
                    continue;
                }
                availableCards[i] = ((UpgradeCard)availableCardsFields[i].getCard()).getEnum().name();
            }
            latestUpgradeShopData.setCards(availableCards);
            serverDataManager.setUpgradeShop(latestUpgradeShopData);
        }
    }

    /**
     * Method for starting the programming phase. This is needed for resetting some parameters in order to prepare for the programming phase.
     */
    public void startProgrammingPhase() {
        incrementTurnCounter();
        playerUpgrading = null;
        latestRegisterData = null;
        board.setCurrentRegister(-1);
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
        board.updateBoard();
    }

    /**
     * Method for when the local player is done choosing their program and has pressed "ready".
     */
    public void finishedProgramming() {
        if (getIsLocalPlayerReadyForNextPhase()) return;
        if (!unresolvedLocalChoices.isEmpty()) return;
        setReadyForNextPhase();
        board.updateBoard();

        localPlayer.fillRestOfRegisters();
        serverDataManager.setPlayerRegister(localPlayer.getProgramFieldCardNames());
        board.updateBoard();
    }

    private void startPlayerActivationPhase() {
        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
        }
        if (checkForRegisterIssues()) return;

        board.setCurrentRegister(board.getCurrentRegister() + 1);
        board.updatePriorityList();

        for (Player player : board.getPlayers()) {
            if (player.equals(localPlayer)) {
                continue;
            }

            Register registers = latestRegisterData.stream().filter(r -> (r.getPlayerId() == player.getPlayerId())).findFirst().orElse(null);
            if (registers != null) {
                player.setRegisters(registers.getMoves()); // Convert String to CardField
            } else {
                System.err.println("Player " + player.getPlayerId() + " has no registers!");
            }
        }

        nextPlayerRegister();
    }

    private boolean checkForRegisterIssues() {
        if (latestRegisterData == null) {
            System.err.println("Latest register data is null!");
            return true;
        }
        if (latestRegisterData.size() != NO_OF_PLAYERS) {
            System.err.println("Latest register data contains " + latestRegisterData.size() + " registers!");
            return true;
        }
        for (Register register : latestRegisterData) {
            if (register.hasNull()) {
                System.err.println("Register " + register + " has null value!");
                return true;
            }
            if (register.getMoves().length != Player.NO_OF_REGISTERS) {
                System.err.println("Register " + register + " has " + register.getMoves().length + " moves!");
                return true;
            }
        }
        return false;
    }



    /*
     *    ### These methods are the main flow of the activation phases. ###
     */
    private void nextPlayerRegister() {
        board.setCurrentPlayer(board.getPriorityList().poll());

        if (shouldDelayForPossibleCardUse()) {
            // Delay before assessing card usages.
            board.getBoardActionQueue().addLast(new ActionWithDelay(() -> setWaitingForCardUse(true), ApplicationSettings.NEXT_PLAYER_REGISTER_DELAY));
        }
        runActionsAndCallback(() -> setAndUpdateChoices(this::handlePlayerRegister));
    }

    private void setWaitingForCardUse(boolean waitingForCardUse) {
        this.waitingForCardUse = waitingForCardUse;
        if (waitingForCardUse) {
            RoboRally.setDebugText(1, "Waiting for card use: true");
            System.out.println("Waiting for card use for player: " + board.getCurrentPlayer().getName());
        } else {
            RoboRally.setDebugText(1, "Waiting for card use: false");
        }

        board.updateBoard();
    }

    private void setAndUpdateChoices(Runnable callback) {
        incrementWaitCounter();
        setWaitingForCardUse(false);
        serverDataManager.setReadyChoice();
        serverDataManager.waitForChoicesAndCallback(callback);
    }

    /**
     * Gets the next player in the priority queue, queues that player's command card and executes it.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void handlePlayerRegister() {
        Player currentPlayer = board.getCurrentPlayer();
        System.out.println("Handling register " + board.getCurrentRegister() + " for player " + currentPlayer.getName());
        if (!currentPlayer.getIsRebooting()) {
            // Handle the players command on the current register. This will queue any command on the register.
            queuePlayerCommandFromCommandCard(currentPlayer);
        }
        // Begin handling the actions.
        handlePlayerActivation();
    }

    /**
     * This method splits up the player activation phase, in order to resume this phase, if it was interrupted.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void handlePlayerActivation() {
        // Run through the queue and execute the player command.
        runActionsAndCallback(this::handleEndOfPlayerTurn);
    }

    /**
     * Handles the end of the current player's register execution.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void handleEndOfPlayerTurn() {
        if (getIsPlayerInteracting()) { // Return and wait for player interaction.
            return;
        }
        if (!board.getPriorityList().isEmpty()) {
            // There are more players in the priorityList. Continue to next player.
            nextPlayerRegister();
        } else {
            setReadyForNextPhase(); // PriorityList is empty, therefore we are ready to end the register.
        }
    }

    /**
     * Handles the end of a register.
     * Board elements and player lasers are queued and then executed.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void startBoardActivationPhase() {
        // Queue board elements and player lasers.
        queueBoardElementsAndRobotLasers();
        handleBoardActivation();
    }

    /**
     * This method splits up the board activation phase, in order to resume this phase, if it was interrupted.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void handleBoardActivation() {
        // Execute board elements and player lasers. When actions have taken place, we go to the next register.
        runActionsAndCallback(this::nextRegister);
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
        // If there are more registers, set the currentRegister and continue to the next player.
        if (currentRegister < Player.NO_OF_REGISTERS - 1) {
            if (!board.isStepMode()) {
                setReadyForNextPhase();
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
        pause.setOnFinished(a -> {
            for (Player player : board.getPlayers()) {
                player.stopRebooting();
                player.getSpace().updateSpace();
            }
            setReadyForNextPhase();
        });  // Small delay before ending activation phase for dramatic effect ;-).
        pause.play();
    }

    /**
     * This method exhausts the action queue by removing the first action, executing it, waits for the action delay, then call itself again to execute the next action in the queue.
     * When the action queue is empty, the callback method is called.
     * If there is a player action, this is interrupted and continued after the player action is handled.
     * @param callback The method to call back to, when the action queue is exhausted.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void runActionsAndCallback(Runnable callback) {
        if (playerInteractionQueue.isEmpty()) {
            LinkedList<ActionWithDelay> actionQueue = board.getBoardActionQueue();
            if (!actionQueue.isEmpty()) { // As long as there are more actions.
                // Handle the next action
                ActionWithDelay nextAction = actionQueue.removeFirst();
                nextAction.getAction(DEBUG_WITH_ACTION_MESSAGE).run();
                int delayInMillis = WITH_ACTION_DELAY ? nextAction.getDelayInMillis() : 0;
                PauseTransition pause = new PauseTransition(Duration.millis(delayInMillis));
                pause.setOnFinished(a -> {
                    EventHandler.event_EndOfAction(this);
                    runActionsAndCallback(callback);
                });
                pause.play();
            } else { // When we have exhausted the actions, call the callback method.
                callback.run();
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
                continueCurrentPhase();
            } catch (UnhandledPhaseInteractionException e) {
                System.err.println(e.getMessage());
            }
        } else {
            currentPlayerInteraction = playerInteractionQueue.poll();
            incrementInteractionCounter();
            RoboRally.setDebugText(9, currentPlayerInteraction.toString());
            currentPlayerInteraction.initializeInteraction();
            board.updateBoard();
        }
    }

    /**
     * Handles what method to go to, if the phase was interrupted.
     * @throws UnhandledPhaseInteractionException If it is not specified what method to go to after player interactions at the current phase.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void continueCurrentPhase() throws UnhandledPhaseInteractionException {
        PlayerInteraction finalInteraction = currentPlayerInteraction;
        currentPlayerInteraction = null;
        RoboRally.setDebugText(9, "");
        board.updateBoard();
        switch (board.getCurrentPhase()) {
            case GamePhase.PLAYER_ACTIVATION -> handlePlayerActivation();
            case GamePhase.BOARD_ACTIVATION -> handleBoardActivation();
            case GamePhase.PROGRAMMING -> board.getCurrentPlayer().stopRebooting();
            default -> throw new UnhandledPhaseInteractionException(board.getCurrentPhase(), finalInteraction);
        }
    }

    /**
     * Handles the player command for a command with multiple options
     *  @author Michael Sylvest Bendtsen, s214954@dtu.dk
     *  @param command the command the player have chosen, and sets the activation phase active again
     */
    public void executeCommandOptionAndContinue(Command command) {
        currentPlayerInteraction.getPlayer().queueCommand(command, false, this);
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
            System.err.println("ERROR - Something went wrong when trying to get command: \"" + commandToQueue + "\" from CommandCard.");
            System.err.println(e.getMessage());
        }
        assert false;
    }

    /**
     * Queues all the board elements on the board, as well as player lasers.
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
        if (sourceType == UPGRADE_CARD_SHOP_FIELD && board.getCurrentPhase() != GamePhase.UPGRADE) return false;

        // Drag from player
        if (sourceField.cardFieldType != UPGRADE_CARD_SHOP_FIELD) {
            if (!sourceField.player.equals(localPlayer)) return false;

            if (board.getCurrentPhase().equals(GamePhase.PROGRAMMING)) {
                return !getIsLocalPlayerReadyForNextPhase();
            } else {
                // Can't drag from register when not programming
                List<CardField> playerProgramFields = Arrays.stream(sourceField.player.getProgramFields()).toList();
                return !playerProgramFields.contains(sourceField);
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
        if (localPlayer.getPlayerId() != targetField.player.getPlayerId()) return false;

        CardField.CardFieldTypes sourceType = sourceField.cardFieldType;
        CardField.CardFieldTypes targetType = targetField.cardFieldType;

        // Can't drag onto shop
        if (targetType == UPGRADE_CARD_SHOP_FIELD) return false;

        // Dragging from shop
        if (sourceType == UPGRADE_CARD_SHOP_FIELD) {
            if (board.getCurrentPhase() != GamePhase.UPGRADE) return false;
            if (sourceField.getCard() instanceof UpgradeCardPermanent && targetType != PERMANENT_UPGRADE_CARD_FIELD) return false;
            if (sourceField.getCard() instanceof UpgradeCardTemporary && targetType != TEMPORARY_UPGRADE_CARD_FIELD) return false;
        }

        // Dragging from player
        if (sourceField.cardFieldType != UPGRADE_CARD_SHOP_FIELD) {
            if (sourceField.player != targetField.player) return false;
            if (sourceType != targetType) return false;

            // Limited card movement when not programming
            List<CardField> playerProgramField = Arrays.stream(sourceField.player.getProgramFields()).toList();
            if (board.getCurrentPhase() != GamePhase.PROGRAMMING) {
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
        if (localPlayer.getPlayerId() != targetField.player.getPlayerId()) return;

        boolean couldMove = true;
        Card sourceCard = sourceField.getCard();
        Card targetCard = targetField.getCard();

        // Buying
        if (sourceField.cardFieldType == UPGRADE_CARD_SHOP_FIELD) {
            if (targetField.cardFieldType == PERMANENT_UPGRADE_CARD_FIELD || targetField.cardFieldType == TEMPORARY_UPGRADE_CARD_FIELD) {
                couldMove = localPlayer.attemptUpgradeCardPurchase(sourceField.getCard(), this);
                if (couldMove && targetCard != null) {
                    localPlayer.removeUpgradeCard((UpgradeCard) targetCard);
                }
            }
        }

        if (couldMove) {
            if (sourceField.cardFieldType != UPGRADE_CARD_SHOP_FIELD) {
                sourceField.setCard(targetCard); // Replaces sourceField card with null if targetCard is null.
            } else {
                sourceField.setCard(null);

                // Removing bought card from available cards
                String[] availableCards = latestUpgradeShopData.getCards();
                for (int i = 0; i < availableCards.length; i++) {
                    if (availableCards[i] != null) {
                        if (availableCards[i].equals(((UpgradeCard)sourceCard).getEnum().name())) {
                            availableCards[i] = null;
                        }
                    }
                }
                latestUpgradeShopData.setCards(availableCards);
            }
            targetField.setCard(sourceCard);

            if (targetField.cardFieldType == PERMANENT_UPGRADE_CARD_FIELD || targetField.cardFieldType == TEMPORARY_UPGRADE_CARD_FIELD) {
                setPlayerCards();
                board.updateBoard();
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
            board.updateBoard();
        } else {
            directionOptionsSpace = null;
        }
    }


    // Updates to the server
    /**
     * Called from view when a space was clicked.
     * @param space The space that was clicked.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void spacePressed(Space space) {
        if (board.getCurrentPhase() == GamePhase.INITIALIZATION) {
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
    public void chooseDirection(Heading direction, GameView gameView) {
        gameView.handleDirectionButtonClicked();
        directionOptionsSpace = null;
        if (board.getCurrentPhase().equals(GamePhase.INITIALIZATION)) {
            localPlayer.setHeading(direction);
            localPlayer.setSpawn(localPlayer.getSpace());
            serverDataManager.setPlayerSpawn(localPlayer.getSpace(), direction.name());
            setReadyForNextPhase();
        } else {
            serverDataManager.setInteraction(currentPlayerInteraction, direction.name());
        }
    }

    public void chooseCommandOption(Command command) {
        serverDataManager.setInteraction(currentPlayerInteraction, command.name());
    }

    public void continueFromInteraction() {
        if (currentPlayerInteraction == null) {
            System.err.println("Error when trying to continueFromInteraction(). The currentPlayerInteraction is null.");
            return;
        }

        switch (currentPlayerInteraction) {
            case RebootInteraction ignored1 -> {
                Heading direction = Heading.valueOf(serverDataManager.getInteraction().getCode());
                currentPlayerInteraction.getPlayer().setHeading(direction);
            }
            case CommandOptionsInteraction ignored2 -> {
                Command command = Command.valueOf(serverDataManager.getInteraction().getCode());
                executeCommandOptionAndContinue(command);
            }
            default -> {}
        }
        currentPlayerInteraction.interactionFinished();
    }

    /**
     * Sets the local player to be ready for the next phase, and sends it to the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void setReadyForNextPhase() {
        GamePhase nextPhase = board.getNextPhase();
        if (getIsLocalPlayerReadyForNextPhase()) return;
        serverDataManager.setReadyForPhase(nextPhase);
    }

    /**
     * Called when the player either buys an Upgrade Card, or clicks the "Finish Upgrading" button.
     * Tells the server to update the player cards and that the player is ready.
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     */
    public void setPlayerCards() {
        playerUpgrading = null;
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
        // Sending the updated shop to the server
        serverDataManager.setUpgradeShop(latestUpgradeShopData);
        setReadyForNextPhase();
    }

    public void tryUseUpgradeCard(UpgradeCard upgradeCard) {
        String resolveStatus = board.getCurrentPhase().equals(GamePhase.PLAYER_ACTIVATION) || board.getCurrentPhase().equals(GamePhase.BOARD_ACTIVATION) ?
                Choice.ResolveStatus.NONE.name() : Choice.ResolveStatus.UNRESOLVED.name();
        ChoiceDTO choiceDTO = new ChoiceDTO(latestGameData.getGameId(), localPlayer.getPlayerId(), upgradeCard.getEnum().name(), waitCounter, resolveStatus);
        unresolvedLocalChoices.add(choiceDTO);
        board.updateBoard();
        serverDataManager.setChoice(choiceDTO);
    }














    public void updateGameWithLatestData() {
        if (!serverDataManager.isConnectedToServer()) return;

        // Updating data
        List<NetworkedDataTypes> changedData = ServerDataManager.getChangedData();
            /*System.out.println();
            System.out.println("||| Changed data: " + changedData + " |||");*/
        if (changedData.contains(NetworkedDataTypes.GAME)) {
            latestGameData = serverDataManager.getUpdatedGame();
        }
        if (changedData.contains(NetworkedDataTypes.PLAYERS)) {
            latestPlayerData = serverDataManager.getUpdatedPlayerMap();
        }

        if (latestGameData == null || latestPlayerData == null) return;

        // Check if any player disconnected
        for (Player client : board.getPlayers()) {
            com.group15.roborally.common.model.Player updatedPlayer = latestPlayerData.get(client.getPlayerId());
            if (updatedPlayer == null) {
                // Player disconnected.
                // TODO: Instead of disconnecting this player, the disconnected player should be removed from the game.
                serverDataManager.disconnectFromServer("Player: \"" + client.getName() + "\" disconnected from the game.", 2000);
                return;
            }
        }

        if (changedData.contains(NetworkedDataTypes.UPGRADE_SHOP)) {
            latestUpgradeShopData = serverDataManager.getUpdatedUpgradeShop();
        }
        if (changedData.contains(NetworkedDataTypes.REGISTERS)) {
            latestRegisterData = serverDataManager.getUpdatedRegisters();
        }
        if (changedData.contains(NetworkedDataTypes.CHOICES)) {
            latestChoiceData = serverDataManager.getUpdatedChoices();
            if (canUseUpgradeCards()) {
                executeUnhandledUpgradeCards();
            }
            removeResolvedChoices();
        }

        // Update the current local phase.
        updateCurrentGamePhase();
    }

    private void removeResolvedChoices() {
        if (!unresolvedLocalChoices.isEmpty()) {
            unresolvedLocalChoices.removeIf(unresolvedChoice -> {
                try {
                    Set<Long> choicePlayerIds = latestChoiceData.stream()
                            .filter(c ->
                                    c.isResolved() && // Received choice is resolved.
                                            c.getCode().equals(unresolvedChoice.code()) && c.getPlayerId() == unresolvedChoice.playerId()) // Received choice matches the locally unresolved choice.
                            .map(c -> Long.parseLong(c.getResolveStatus()))
                            .collect(Collectors.toSet());
                    choicePlayerIds.add(localPlayer.getPlayerId());
                    return choicePlayerIds.containsAll(latestPlayerData.keySet());
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing playerId of choice: \n" + unresolvedChoice.toString());
                    return false;
                }
            });
        }
    }

    /**
     * Redirection of update logic for the current local GamePhase.
     */
    private void updateCurrentGamePhase() {
        if (handlingPrePhase) return;

        switch (board.getCurrentPhase()) {
            case GamePhase.INITIALIZATION -> updateInitialization();
            case GamePhase.PROGRAMMING -> updateProgramming();
            case GamePhase.UPGRADE -> updateUpgrading();
        }
        board.updateBoard();

        // Check if all players are ready to switch to the next GamePhase. If they all are, switch locally and call initial GamePhase method.
        if (canStartNextPhase()) {
            handlePrePhase();
        }
    }

    private void updateInitialization() {
        // Check if all players have set their spawn point
        for (Player client : board.getPlayers()) {
            com.group15.roborally.common.model.Player updatedPlayer = latestPlayerData.get(client.getPlayerId());
            if (updatedPlayer == null) continue; // Player disconnected.
            if (client.getSpawnPoint() != null) continue; // Client already has a spawn point

            int[] clientSpawnPoint = updatedPlayer.getSpawnPoint();
            if (clientSpawnPoint == null) continue; // Client hasn't chosen a spawn point.

            Space clientSpawnPosition = board.getSpace(clientSpawnPoint[0], clientSpawnPoint[1]);
            if (clientSpawnPosition == null) continue; // Can't find space at position.

            // Local player
            if (client.equals(localPlayer)) {
                if (getIsLocalPlayerReadyForNextPhase()) continue; // Local player has already sat their spawn point.
                // Local player direction option
                setDirectionOptionsPane(client, clientSpawnPosition);
            }

            // Setting player at selected position
            client.setSpace(clientSpawnPosition);
            board.updateBoard();

            // Heading
            String playerSpawnDirection = updatedPlayer.getSpawnDirection();
            if (playerSpawnDirection == null || playerSpawnDirection.isBlank()) continue; // Client hasn't chosen a direction.

            // Setting spawnPoint
            Heading clientHeading = Heading.valueOf(playerSpawnDirection);
            client.setHeading(clientHeading);
            client.setSpawn(clientSpawnPosition);
        }
    }

    private void updateProgramming() {
        if (latestRegisterData == null) return;
        if (latestRegisterData.isEmpty()) return;

        if (latestRegisterData.stream().anyMatch(r -> !r.hasNull())) {
            if (countdownTimer != null) return;

            Instant startTimeOfCountdown = Instant.now();
            countdownTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    int newCountdownTimeLeft = Math.clamp(30 - (int)java.time.Duration.between(startTimeOfCountdown, Instant.now()).toSeconds(), 0, 30);
                    if (newCountdownTimeLeft != countdownTimeLeft) {
                        countdownTimeLeft = newCountdownTimeLeft;
                        board.updateBoard();
                        if (countdownTimeLeft <= 0) finishedProgramming();
                    }
                }
            };
            countdownTimer.start();
        }
    }

    /**
     * Updates proxy players' upgrade cards and sets the available card in the upgrade shop with the data from the server.
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void updateUpgrading() {
        // Updating proxy players upgrade cards.
        for (Player client : board.getPlayers()) {
            com.group15.roborally.common.model.Player updatedPlayer = latestPlayerData.get(client.getPlayerId());
            if (updatedPlayer == null || client.equals(localPlayer))
                continue;

            // Updating proxy player's cards
            String[] permCardsStr = updatedPlayer.getPermCards();
            String[] tempCardsStr = updatedPlayer.getTempCards();
            client.updateUpgradeCards(permCardsStr, tempCardsStr, this);
        }

        if (latestUpgradeShopData.getTurn() != turnCounter) return;

        // Set available cards locally
        board.getUpgradeShop().setAvailableCards(latestUpgradeShopData.getCards());

        // Finding whose turn it is to upgrade.
        int upgradeTurn = 0;
        for (int i = 0; i < board.getPriorityList().size(); i++) {
            Player client = board.getPriorityList().stream().toList().get(i);
            com.group15.roborally.common.model.Player updatedPlayer = latestPlayerData.get(client.getPlayerId());
            if (updatedPlayer.getReadyForPhase().equals(GamePhase.PROGRAMMING)) {
                upgradeTurn++;
            } else {
                break;
            }
        }

        // Update view with the upgrading player
        if (upgradeTurn < NO_OF_PLAYERS) {
            playerUpgrading = board.getPriorityList().stream().toList().get(upgradeTurn);
        }

        // If the shop is empty
        if (latestUpgradeShopData == null || Arrays.stream(latestUpgradeShopData.getCards()).allMatch(str -> str == null || str.isBlank())) {
            System.out.println("Shop is empty. Readying up for next phase.");
            setReadyForNextPhase();
        }
    }

    private boolean canStartNextPhase() {
        if (handlingPrePhase) return false;
        if (currentPlayerInteraction != null || !playerInteractionQueue.isEmpty()) return false;

        boolean allAreReady = latestPlayerData.values().stream().allMatch(this::getIsPlayerReadyForNextPhase);

        if (board.getCurrentPhase().equals(GamePhase.PROGRAMMING)) {
            boolean receivedAllRegisters =
                    latestRegisterData != null &&
                            latestRegisterData.size() == NO_OF_PLAYERS &&
                            latestRegisterData.stream().noneMatch(Register::hasNull);
            RoboRally.setDebugText(0, "allAreReady: " + allAreReady + ". receivedAllRegisters: " + receivedAllRegisters + ". " + (latestRegisterData == null ? "registers are null" : ("No of registers: " + latestRegisterData.size())));
            return allAreReady && receivedAllRegisters;
        }
        RoboRally.setDebugText(0, "allAreReady: " + allAreReady);
        return allAreReady;
    }

    private void handlePrePhase() {
        System.out.println();

        incrementPhaseCounter();
        handlingPrePhase = true;
        RoboRally.setDebugText(6, "handlingPrePhase: " + true);
        GamePhase newPhase = board.getNextPhase();
        if (serverDataManager.isHost()) {
            serverDataManager.setGamePhase(newPhase);
        }
        board.setCurrentPhase(newPhase);
        serverDataManager.setCurrentPhase(newPhase);
        RoboRally.setDebugText(2, "GamePhase: " + newPhase);

        startNextPhase();
    }

    private void startNextPhase() {
        handlingPrePhase = false;
        RoboRally.setDebugText(6, "handlingPrePhase: " + false);

        switch (board.getCurrentPhase()) {
            case GamePhase.PROGRAMMING -> startProgrammingPhase();
            case GamePhase.PLAYER_ACTIVATION -> startPlayerActivationPhase();
            case GamePhase.BOARD_ACTIVATION -> startBoardActivationPhase();
            case GamePhase.UPGRADE -> startUpgradingPhase();
        }
    }

    private void executeUnhandledUpgradeCards() {
        latestChoiceData = serverDataManager.getUpdatedChoices();
        List<Choice> unhandledChoices = new ArrayList<>(latestChoiceData);
        unhandledChoices.removeAll(executedChoices);

        for (Choice choice : unhandledChoices) {
            executedChoices.add(choice);
            if (choice.getCode().equals(Choice.READY_CHOICE)) continue;
            if (choice.isResolved()) continue;

            Player player = board.getPlayers().stream().filter(p -> p.getPlayerId() == choice.getPlayerId()).findFirst().orElse(null);
            if (player == null) {
                System.err.println("Player with playerId " + choice.getPlayerId() + " can not be found for code " + choice.getCode() + "!");
                continue;
            }

            // If it's another player's unresolved code, we resolve it by sending it back with this player's ID.
            if (choice.getPlayerId() != localPlayer.getPlayerId() && choice.getResolveStatus().equals(Choice.ResolveStatus.UNRESOLVED.name())) {
                ChoiceDTO resolvedChoiceDTO = new ChoiceDTO(
                        choice.getGameId(),
                        choice.getPlayerId(),
                        choice.getCode(),
                        choice.getWaitCount(),
                        String.valueOf(localPlayer.getPlayerId())
                );
                serverDataManager.setChoice(resolvedChoiceDTO);
            }

            for (UpgradeCard upgradeCard : player.getUpgradeCards()) {
                if (upgradeCard.getEnum().name().equals(choice.getCode())) {
                    upgradeCard.activate();
                    break;
                }
            }
        }
    }

    public boolean getIsLocalPlayerReadyForNextPhase() {
        return getIsPlayerReadyForNextPhase(localPlayer);
    }

    public boolean getIsPlayerReadyForNextPhase(Player player) {
        return getIsPlayerReadyForNextPhase(latestPlayerData.get(player.getPlayerId()));
    }
    public boolean getIsPlayerReadyForNextPhase(com.group15.roborally.common.model.Player player) {
        GamePhase nextPhase = board.getNextPhase();
        return player.getReadyForPhase().equals(nextPhase) || player.getPhaseCount() > phaseCounter;
    }

    public boolean canUseUpgradeCards() {
        return (!board.getCurrentPhase().equals(GamePhase.PLAYER_ACTIVATION) && !board.getCurrentPhase().equals(GamePhase.BOARD_ACTIVATION) && !getIsLocalPlayerReadyForNextPhase()) ||
                (board.getCurrentPhase().isPhaseToWaitBefore() && waitingForCardUse);
    }

    public boolean shouldDelayForPossibleCardUse() {
        boolean phaseToWaitBefore = board.getCurrentPhase().isPhaseToWaitBefore();
        boolean useCardPresent = board.getPlayers().stream().anyMatch(player -> player.getUpgradeCards().stream().anyMatch(UpgradeCard::isEnabled));
        RoboRally.setDebugText(10, "shouldDelayForPossibleCardUse?: " + phaseToWaitBefore + " && " + useCardPresent);
        return phaseToWaitBefore && useCardPresent;
    }

    public int getCountdownTime() {
        if (countdownTimer == null) return -1;
        return countdownTimeLeft;
    }
}
