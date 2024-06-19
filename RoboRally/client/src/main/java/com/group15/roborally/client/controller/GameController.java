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
import com.group15.roborally.client.model.player_interaction.*;
import com.group15.roborally.client.model.upgrade_cards.*;
import com.group15.roborally.client.view.BoardView;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.server.model.Game;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.group15.roborally.client.model.CardField.CardFieldTypes.*;
import static com.group15.roborally.client.model.Phase.*;
import static com.group15.roborally.client.ApplicationSettings.*;
import static com.group15.roborally.client.BoardOptions.*;

/**
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class GameController implements Observer {
    public final Board board;
    private final Runnable gameOverMethod;
    private final Player localPlayer;
    private final NetworkingController networkingController;

    private Space directionOptionsSpace;
    private String winnerName;
    private Image winnerIMG;
    private boolean isRegisterPlaying = false;

    // ACTION PHASE VARIABLES
    public int moveStep = 0;

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
     * @param gameOverMethod The method for calling game over.
     */
    public GameController(@NotNull Board board, Player localPlayer, NetworkingController networkController, Runnable gameOverMethod) {
        this.board = board;
        this.gameOverMethod = gameOverMethod;
        this.localPlayer = localPlayer;
        this.networkingController = networkController;
        this.networkingController.attach(this);
    }

    /**
     * Method for starting the game. Called when players have chosen a start space and direction.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void beginGame() {
        for (Player player : board.getPlayers()) {
            for (UpgradeCard card : STARTING_UPGRADE_CARDS) {
                player.tryAddFreeUpgradeCard(card, this);
            }
        }

        startProgrammingPhase();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * 
     * ACTIVATION PHASE
     * 
     */
    /////////////////////////////////////////////////////////////////////////////////////////////////////


    public void startActivationPhase() {
        /*
         * TODO: Next step is activation phase - implement with server logic
         */
        board.setCurrentPhase(PLAYER_ACTIVATION);
        board.setCurrentRegister(0);
        moveStep = 0;
        executeRegisters();
        exitActivationPhase();
    }

    public void exceuteRegisterRound(){
        board.updatePriorityList();
        executeMoves();
    }

    public void executeRegisters(){
        if(board.getCurrentRegister() <= Player.NO_OF_REGISTERS){
            board.updatePriorityList();
            // Recursive excution of moves in same register index
            executeMoves();
            setIsRegisterPlaying(false);
            board.setCurrentRegister(board.getCurrentRegister() + 1);
            endOfRegister();
            executeRegisters();
        } 
    }

    public void executeMoves(){
        if(!board.getPriorityList().isEmpty()){
            board.setCurrentPhase(PLAYER_ACTIVATION);
            board.setCurrentPlayer(board.getPriorityList().poll());
            Player currentPlayer = board.getCurrentPlayer();
            if (!currentPlayer.getIsRebooting()) {
                // Handle the players command on the current register. This will queue any command on the register.
                queuePlayerCommandFromCommandCard(currentPlayer);
            }
            runActions(board.getBoardActionQueue());
            moveStep++;
            executeMoves();
        }
    }

    /**
     * Handles the end of a register.
     * Here we queue board elements and player lasers, then execute them.
     * Afterwards, we set the next register, calling handleNextPlayerTurn() again.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void endOfRegister() {
        board.setCurrentPhase(BOARD_ACTIVATION);
        // Queue board elements and player lasers.
        queueBoardElementsAndRobotLasers();
        runActions(board.getBoardActionQueue());
    }

    public void runActions(LinkedList<ActionWithDelay> actionQueue){
        if(!actionQueue.isEmpty()){
            ActionWithDelay nextAction = actionQueue.removeFirst();
            nextAction.getAction(DEBUG_WITH_ACTION_MESSAGE).run();
            int delayInMillis = nextAction.getDelayInMillis();
            PauseTransition pause = new PauseTransition(Duration.millis(delayInMillis));
            pause.setOnFinished(event -> {
                EventHandler.event_EndOfAction(this);
                runActions(actionQueue);
            });
            if (WITH_ACTION_DELAY) {
                pause.play();
            }
        }
    }

    /**
     * Handles what happens when a round is done.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void exitActivationPhase() {
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
     * @return the current PlayerInteraction.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public PlayerInteraction getCurrentPlayerInteraction() {
        return currentPlayerInteraction;
    }

    /**
     * Handles the player command for a command with multiple options
     *  @author Michael Sylvest Bendtsen, s214954@dtu.dk
     *  @param option the option the player have chosen, and sets the activation phase active again
     */
    public void executeCommandOptionAndContinue(Command option) {
        currentPlayerInteraction.player.queueCommand(option, false, this);
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
            e.printStackTrace();
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

    

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * 
     * PROGRAMMING PHASE
     * 
     */
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method for starting the upgrade phase.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void startUpgradingPhase() {
        board.getUpgradeShop().refillAvailableCards();
        board.setCurrentPhase(UPGRADE);
        board.updateBoard();
    }

    /**
     * Method for starting the programming phase. This is needed for resetting some parameters in order to prepare for the programming phase.
     */
    public void startProgrammingPhase() {
        board.setCurrentPhase(PROGRAMMING);

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
     * Method for when the programming phase ends.
     */
    // public void finishProgrammingPhase() {
    //     board.setCurrentPhase(PLAYER_ACTIVATION);

    //     makeProgramFieldsInvisible();
    //     makeProgramFieldsVisible(0);


    //     if (DRAW_ON_EMPTY_REGISTER) {
    //         for (com.group15.roborally.client.model.Player player : board.getPlayers()) {
    //             player.fillRestOfRegisters();
    //         }
    //     }
    // }

    public void finishProgrammingPhase() {
        networkingController.updateRegister(localPlayer.getPlayerId(), localPlayer.getProgramFieldNames(), board.getTurnCounter());
        networkingController.updateRegisters(this::enterActivationPhase);
    }

    public void enterActivationPhase() {
        String[] registers;
        for (Player player : board.getPlayers()){
            if (player.equals(localPlayer)){
                continue;
            }
            registers = networkingController.getRegistersFromPlayer(player.getPlayerId());
            player.setRegisters(registers); // Convert String to CardField
        }
        startActivationPhase();
    }

    /**
     * Returns the winners image.
     * @return Image
     * @author Maximillian Bjørn Mortensen
     */
    public Image getWinnerIMG(){
        return winnerIMG;
    }

    /**
     * Returns the winners name.
     * @return String
     * @author Maximillian Bjørn Mortensen
     */
    public String getWinnerName() {
        return winnerName;
    }

    /**
     * Sets the parameters as the winner
     * @param winnerName
     * @param winnerIMG
     * @author Maximillian Bjørn Mortensen
     */
    public void setWinner(String winnerName, Image winnerIMG) {
        this.winnerName = winnerName;
        this.winnerIMG = winnerIMG;
        gameOverMethod.run();
    }

    /**
     * Checks whether a player has reached the last checkpoint.
     * @param player The player who reached a checkpoint.
     * @param number The checkpoint number.
     */
    public void checkpointReached(Player player, int number) {
        if(number >= board.getNumberOfCheckpoints()){
            setWinner(player.getName(), player.getCharImage());
        }
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
                if (playerProgramField.contains(sourceField)) return false;
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
                if (((CommandCard)(sourceField.getCard())).command == Command.AGAIN && targetField.index == 1) return false;
            }
        }

        return true;
    }

    /**
     * Method for moving a card from one CardField to another.
     * @param sourceField The CardField being moved from.
     * @param targetField The CardField being moved to.
     * @return Whether the card could be moved. Used for purchase of cards.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
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


    // Updates to server
    /**
     * Called from view when a space was clicked.
     * @param space The space that was clicked.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void spacePressed(Space space) {
        if (board.getCurrentPhase() == INITIALIZATION) {
            if (space.getBoardElement() instanceof BE_SpawnPoint) {
                if (space.getPlayer() == null) {
                    networkingController.setPlayerSpawn(space);
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
        } else {
            currentPlayerInteraction.player.setHeading(direction);
            currentPlayerInteraction.interactionFinished();
        }
    }


    // Updates from server
    @Override
    public void update(Subject subject) {
        if (subject.equals(networkingController)) {
            Game updatedGame = networkingController.getUpdatedGame();
            HashMap<Long, com.group15.roborally.server.model.Player> updatedPlayers = networkingController.getUpdatedPlayerMap();

            for (Player client : board.getPlayers()) {
                com.group15.roborally.server.model.Player updatedPlayer = updatedPlayers.get(client.getPlayerId());
                if (updatedPlayer == null) {
                    // Player disconnected.
                    System.out.println("Player: \"" + client.getName() + "\" disconnected from the game.");
                    // TODO: Handle player disconnect.
                }
            }

            switch (updatedGame.getPhase()) {
                case INITIALIZATION -> updateInitialization(updatedPlayers);
            }
        }
    }

    private void updateInitialization(HashMap<Long, com.group15.roborally.server.model.Player> updatedPlayers) {
        for (Player client : board.getPlayers()) {
            com.group15.roborally.server.model.Player updatedPlayer = updatedPlayers.get(client.getPlayerId());
            if (updatedPlayer == null) {
                // Player disconnected.
                continue;
            }

            // Position
            int[] clientSpawnPoint = updatedPlayer.getSpawnPoint();
            if (clientSpawnPoint != null) {
                Space clientSpawnPosition = board.getSpace(clientSpawnPoint[0], clientSpawnPoint[1]);
                // Heading
                Heading clientHeading = client.getHeading();
                if (clientHeading != null) {
                    // Setting spawnPoint
                    client.setSpawn(clientSpawnPosition);
                    if (clientSpawnPosition.getBoardElement() instanceof BE_SpawnPoint spawnPoint) {
                        spawnPoint.setColor(client);
                        board.updateBoard();
                    }
                } else {
                    if (client.equals(localPlayer)) {
                        // Local player direction option
                        setDirectionOptionsPane(clientSpawnPosition);
                    }
                }
            }
        }
    }

    /**
     * Method for setting the direction pane position at a space.
     * @param space The space that the direction pane should appear at.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setDirectionOptionsPane(Space space) {
        space.updateSpace();
        directionOptionsSpace = space;
        board.updateBoard();
    }

    /**
     * Method for the BoardView to get the new direction pane space.
     * @return The space to put the direction pane at.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public Space getDirectionOptionsSpace() {
        return directionOptionsSpace;
    }
}
