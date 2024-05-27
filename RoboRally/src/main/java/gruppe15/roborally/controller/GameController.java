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

import gruppe15.roborally.exceptions.UnhandledPhaseInteractionException;
import gruppe15.roborally.model.*;
import gruppe15.roborally.model.boardelements.*;
import gruppe15.roborally.model.player_interaction.PlayerInteraction;
import gruppe15.roborally.model.upgrade_cards.*;
import gruppe15.roborally.view.BoardView;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static gruppe15.roborally.model.CardField.CardFieldTypes.*;
import static gruppe15.roborally.model.Phase.*;
import static gruppe15.roborally.GameVariables.*;
import static gruppe15.roborally.GameSettings.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {
    public final Board board;
    private final AppController appController;

    private Space directionOptionsSpace;
    private String winnerName;
    private Image winnerIMG;
    private boolean isRegisterPlaying = false;

    // Player interaction
    private final Queue<PlayerInteraction> playerInteractionQueue = new LinkedList<>();
    private PlayerInteraction currentPlayerInteraction = null;

    public boolean getIsRegisterPlaying() {
        return isRegisterPlaying;
    }
    private void setIsRegisterPlaying(boolean isRegisterPlaying) {
        this.isRegisterPlaying = isRegisterPlaying;
        board.updateBoard();
    }

    /**
     * Constructor method for GameController.
     * @param board The current board
     * @param appController The current AppController
     */
    public GameController(@NotNull Board board, AppController appController) {
        this.board = board;
        this.appController = appController;
    }

    /**
     * Method for starting the game. Called when players have chosen a start space and direction.
     */
    private void beginGame() {
        for (Player player : board.getPlayers()) {
            for (UpgradeCard card : STARTING_UPGRADE_CARDS) {
                player.tryAddFreeUpgradeCard(card, this);
            }
        }

        startProgrammingPhase();
    }

    /**
     * Method for starting the upgrade phase.
     */
    public void startUpgradingPhase() {
        board.getUpgradeShop().refillAvailableCards();
        board.setPhase(UPGRADE);
        board.updateBoard();
    }

    /**
     * Method for starting the programming phase. This is needed for resetting some parameters in order to prepare for the programming phase.
     */
    public void startProgrammingPhase() {
        board.setPhase(PROGRAMMING);

        board.setCurrentRegister(0);
        board.updatePriorityList();
        board.setCurrentPlayer(board.getPriorityList().peek());

        for (int i = 0; i < board.getNoOfPlayers(); i++) {
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

    public void finishProgrammingPhase() {
        board.setPhase(PLAYER_ACTIVATION);

        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);

        for (Player player : board.getPlayers()) {
            player.fillRestOfRegisters();
        }
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_OF_REGISTERS) {
            for (int i = 0; i < board.getNoOfPlayers(); i++) {
                Player player = board.getPlayer(i);
                CardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getNoOfPlayers(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_OF_REGISTERS; j++) {
                CardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    public void executePrograms() {
        board.setStepMode(false);
        handlePlayerRegister();
    }

    public void executeRegister() {
        board.setStepMode(true);
        handlePlayerRegister();
    }

    /**
     *     These methods are the main flow of a register.
     */
    private void handlePlayerRegister() {
        board.setPhase(PLAYER_ACTIVATION);
        System.out.println();
        setIsRegisterPlaying(true);
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
     * This method splits up handlePlayerRegister(), in order to call this again, if the command is a PLAYER_INTERACTION.
     */
    private void handlePlayerActions() {
        // Run through the queue and execute the player command.
        runActionsAndCallback(this::handleEndOfPlayerTurn, board.getBoardActionQueue());
    }

    private void handleEndOfPlayerTurn() {
        if (!playerInteractionQueue.isEmpty()) {
            // Return and wait for player interaction.
            return;
        }
        // When player command is executed, check if there are more player turns this register.
        if (!board.getPriorityList().isEmpty()) {
            // There are more players in the priorityList. Continue to next player.
            handlePlayerRegister();
        } else {
            // priorityList is empty, therefore we end the register.
            handleEndOfRegister();
        }
    }

    /**
     * This is the flow of the end of a register.
     * Here we queue board elements and player lasers, then execute them.
     * Afterwards, we set the next register, calling handleNextPlayerTurn() again.
     */
    private void handleEndOfRegister() {
        board.setPhase(BOARD_ACTIVATION);
        // Queue board elements and player lasers.
        queueBoardElementsAndRobotLasers();
        handleBoardElementActions();
    }
    private void handleBoardElementActions() {
        // Execute board elements and player lasers. When actions have taken place, we go to the next register.
        runActionsAndCallback(this::nextRegister, board.getBoardActionQueue());
    }
    public void nextRegister() {
        if (!playerInteractionQueue.isEmpty()) {
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
            setIsRegisterPlaying(false);
            board.updateBoard();
            if (!board.isStepMode()) {
                handlePlayerRegister();
            }
        } else {
            handleEndOfRound();
        }
    }

    private void handleEndOfRound() {
        // If all registers are done
        PauseTransition pause = new PauseTransition(Duration.millis(NEXT_REGISTER_DELAY));
        pause.setOnFinished(event -> {
            for (Player player : board.getPlayers()) {
                player.stopRebooting();
                player.getSpace().updateSpace();
            }
            startUpgradingPhase();
            setIsRegisterPlaying(false);
        });  // Small delay before ending activation phase for dramatic effect ;-).
        pause.play();
    }

    private void runActionsAndCallback(Runnable callback, LinkedList<ActionWithDelay> actionQueue) {
        if (playerInteractionQueue.isEmpty()) {
            if (board.getPhase() == PLAYER_ACTIVATION || board.getPhase() == BOARD_ACTIVATION) {
                if (!actionQueue.isEmpty()) { // As long as there are more actions.
                    // Handle the next action
                    ActionWithDelay nextAction = actionQueue.removeFirst();
                    nextAction.getAction(WITH_ACTION_MESSAGE).run();
                    Duration delay = nextAction.getDelay();
                    PauseTransition pause = new PauseTransition(delay);
                    pause.setOnFinished(event -> {
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
                System.out.println("Possible error? Phase is: \"" + board.getPhase() + "\", but currently running actions.");
            }
        } else {
            handleNextInteration();
        }
    }

    public void handleNextInteration() {
        // Check if there are more interactions.
        if (playerInteractionQueue.isEmpty()) {
            // If not, continue
            try {
                continueActions();
            } catch (UnhandledPhaseInteractionException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        } else {
            currentPlayerInteraction = playerInteractionQueue.poll();
            currentPlayerInteraction.initializeInteraction();
        }
    }

    private void continueActions() throws UnhandledPhaseInteractionException {
        if (board.getPhase() == PLAYER_ACTIVATION) {
            currentPlayerInteraction = null;
            handlePlayerActions();
        } else if (board.getPhase() == BOARD_ACTIVATION) {
            currentPlayerInteraction = null;
            handleBoardElementActions();
        } else {
            throw new UnhandledPhaseInteractionException(board.getPhase(), currentPlayerInteraction);
        }
    }

    public PlayerInteraction getCurrentPlayerInteraction() {
        return currentPlayerInteraction;
    }

    private void queuePlayerCommandFromCommandCard(Player currentPlayer) {
        Command commandToQueue = null;
        try {
            int currentRegister = board.getCurrentRegister();
            CommandCard card = (CommandCard) currentPlayer.getProgramField(currentRegister).getCard();
            commandToQueue = card.command;
            currentPlayer.queueCommand(commandToQueue, this);
        } catch (Exception e) {
            System.out.println("ERROR - Something went wrong when trying to get command: \"" + commandToQueue + "\" from CommandCard.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        assert false;
    }

    /**
     * Handle the player command for a command with multiple options
     *
     *  @author Michael Sylvest Bendtsen, s214954@dtu.dk
     *  @param option the option the player have chosen, and sets the activation phase active again
     */
    public void executeCommandOptionAndContinue(Command option) {
        currentPlayerInteraction.player.queueCommand(option, false, this);
        currentPlayerInteraction.interactionFinished();
    }

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

    public Queue<PlayerInteraction> getPlayerInteractionQueue() {
        return playerInteractionQueue;
    }

    /**
     * Method for making a new player interaction. This stops all "_ACTIVATION" loops and begins the interaction.
     * When the player interaction is done, the previous "_ACTIVATION" loop will continue.
     */
    public void addPlayerInteraction(PlayerInteraction interaction) {
        playerInteractionQueue.offer(interaction);
        board.updateBoard();
    }

    /**
     * returns the winners image
     * @return Image
     * @author Maximillian Bjørn Mortensen
     */
    public Image getWinnerIMG(){
        return winnerIMG;
    }

    /**
     * returns the winners name
     * @return String
     * @author Maximillian Bjørn Mortensen
     */
    public String getWinnerName() {
        return winnerName;
    }

    /**
     * sets the paramaters as the winner
     * @param winnerName
     * @param winnerIMG
     * @author Maximillian Bjørn Mortensen
     */
    public void setWinner(String winnerName, Image winnerIMG){
        this.winnerName = winnerName;
        this.winnerIMG = winnerIMG;
        appController.gameOver();
    }

    public void checkpointReached(Player player, int number) {
        if(number >= board.getNumberOfCheckpoints()){
            setWinner(player.getName(), player.getCharImage());
        }
    }











    public void setDirectionOptionsPane(Space space, Player playerRebooting) {
        // Quirk fix for showing the player when another player is on the respawning players' spawn
        Player playerOnSpawnPoint = space.getPlayer();
        if (playerOnSpawnPoint != null) {
            if (playerOnSpawnPoint != playerRebooting) {
                if (playerRebooting.getSpawnPoint() == space) {
                    Space otherSpawnPoint = playerOnSpawnPoint.getSpawnPoint();
                    playerOnSpawnPoint.setSpace(otherSpawnPoint);
                    playerOnSpawnPoint.setTemporarySpace(otherSpawnPoint);
                    EventHandler.event_PlayerReboot(playerOnSpawnPoint, false, this);
                    otherSpawnPoint.updateSpace();
                }
            }
        }
        playerRebooting.goToTemporarySpace();
        space.updateSpace();

        directionOptionsSpace = space;
        board.updateBoard();
    }

    public void spacePressed(MouseEvent event, Space space) {
        if (board.getPhase() == INITIALIZATION) {
            if (space.getBoardElement() instanceof BE_SpawnPoint) {
                Player currentPlayer = board.getCurrentPlayer();
                if (space.getPlayer() == null) {
                    space.setPlayer(currentPlayer);
                    setDirectionOptionsPane(space, currentPlayer);
                }
            }
        }

        // Debugging
        if (board.getPhase() != INITIALIZATION) {
            if (space.getPlayer() == null) {
                if (event.isShiftDown()) {
                    space.setPlayer(board.getPlayer(1));
                } else if (event.isControlDown()) {
                    space.setPlayer(board.getPlayer(0));
                }
            }
        }
    }

    public void initializeDirectionButton(Button button, BoardView BV) {
        Heading direction = Heading.valueOf(button.getId());
        button.setOnMouseClicked(event -> {
            chooseDirection(direction, BV);
        });
    }

    public Space getDirectionOptionsSpace() {
        return directionOptionsSpace;
    }

    /**
     * This method is called when the player has chosen a direction for the robot.
     * The direction is set for the current player and the next player is set as the current player.
     * If the next player has a spawn point, the programming phase is started.
     * 
     * @param direction the direction chosen by the player
     * @autor Tobias Nicolai Frederiksen, s235086@dtu.dk
     */
    public void chooseDirection(Heading direction, BoardView BV) {
        BV.handleDirectionButtonClicked();
        directionOptionsSpace = null;
        if (board.getPhase() == INITIALIZATION) {
            Player player = board.getCurrentPlayer();

            Space spawnSpace = player.getSpace();
            player.setSpawn(spawnSpace);
            player.setSpace(spawnSpace);

            if (spawnSpace.getBoardElement() instanceof BE_SpawnPoint spawnPoint) {
                spawnPoint.setColor(player);
                BV.initializePlayerSpawnSpaceView(spawnSpace);
            }
            player.setHeading(direction);

            int nextPlayerIndex = (board.getPlayerNumber(player) + 1) % board.getNoOfPlayers();
            Player nextPlayer = board.getPlayer(nextPlayerIndex);
            board.setCurrentPlayer(nextPlayer);
            if (nextPlayer.getSpawnPoint() != null) {
                //startUpgradingPhase();
                beginGame();
            }
        } else {
            currentPlayerInteraction.player.setHeading(direction);
            currentPlayerInteraction.interactionFinished();
        }
    }

    public boolean canDragCard(CardField sourceField) {
        if (sourceField == null) return false;
        if (sourceField.getCard() == null) return false;
        CardField.CardFieldTypes sourceType = sourceField.cardFieldType;

        // Drag from shop
        if (sourceType == UPGRADE_CARD_SHOP_FIELD && board.getPhase() != UPGRADE) return false;

        // Drag from player
        if (sourceField.cardFieldType != UPGRADE_CARD_SHOP_FIELD) {
            // Limited card movement when not programming
            assert sourceField.player != null;
            List<CardField> playerProgramField = Arrays.stream(sourceField.player.getProgramFields()).toList();
            if (board.getPhase() != PROGRAMMING) {
                if (playerProgramField.contains(sourceField)) return false;
            }
        }

        return true;
    }

    public boolean canDropCard(CardField sourceField, CardField targetField) {
        if (sourceField == null || targetField == null) return false;
        CardField.CardFieldTypes sourceType = sourceField.cardFieldType;
        CardField.CardFieldTypes targetType = targetField.cardFieldType;

        // Can't drag onto shop
        if (targetType == UPGRADE_CARD_SHOP_FIELD) return false;

        // Dragging from shop
        if (sourceType == UPGRADE_CARD_SHOP_FIELD) {
            if (board.getPhase() != UPGRADE) return false;
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
            if (board.getPhase() != PROGRAMMING) {
                if (playerProgramField.contains(sourceField) || playerProgramField.contains(targetField)) return false;
            }

            // Can't put again command on first register
            if (sourceField.cardFieldType == COMMAND_CARD_FIELD) {
                if (((CommandCard)(sourceField.getCard())).command == Command.AGAIN && targetField.index == 1) return false;
            }
        }

        return true;
    }

    public boolean moveCard(@NotNull CardField sourceField, @NotNull CardField targetField) {
        boolean couldMove = true;
        Card sourceCard = sourceField.getCard();
        Card targetCard = targetField.getCard();

        // Buying
        if (sourceField.cardFieldType == UPGRADE_CARD_SHOP_FIELD) {
            assert targetField.player != null;
            Player player = targetField.player;
            boolean canBuyUpgradeCard = player.attemptUpgradeCardPurchase(sourceField, this);
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
        }

        return couldMove;
    }
}
