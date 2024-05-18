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

import gruppe15.roborally.model.*;
import gruppe15.roborally.model.boardelements.*;
import gruppe15.roborally.model.utils.ImageUtils;
import gruppe15.roborally.view.BoardView;
import gruppe15.roborally.view.SpaceView;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static gruppe15.roborally.model.Phase.*;
import static gruppe15.roborally.model.utils.Constants.SPACE_HEIGHT;
import static gruppe15.roborally.model.utils.Constants.SPACE_WIDTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {
    public final Board board;
    private final AppController appController;

    private final Queue<Player> playersRebooting = new ArrayDeque<>();
    private Space directionOptionsSpace;
    private String winnerName;
    private Image winnerIMG;
    private boolean turnPlaying = false;

    // Actions
    private final LinkedList<ActionWithDelay> actionQueue = new LinkedList<>();
    private final int nextRegisterDelay = 1000; // In milliseconds.
    private final boolean WITH_ACTION_DELAY = true;
    private final boolean WITH_ACTION_MESSAGE = false;


    public boolean getIsTurnPlaying() {
        return turnPlaying;
    }

    /**
     *
     * @param board The current board
     * @param appController The current AppController
     */
    public GameController(@NotNull Board board, AppController appController) {
        this.board = board;
        this.appController = appController;
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

    /**
     * Takes the current player of the board and sets the players position to the given space
     * if the space is free. The current player is then set to the player following the current player.
     *
     * @param player
     * @param nextSpace the space to which the current player should move
     * @return void
     * @autor Tobias Nicolai Frederiksen, s235086@dtu.dk
     */
    public void movePlayerToSpace(Player player, Space nextSpace) {
        // TODO Task1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free())
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if the player is moved
        Space currentSpace = player.getSpace();
        if (currentSpace == null) {
            System.out.println("ERROR: Current space of " + player.getName() + " is null. Cannot move player.");
            return;
        }

        boolean couldMove = true;
        if (nextSpace != null) {
            boolean isWallBetween = currentSpace.getIsWallBetween(nextSpace);
            if (!isWallBetween) { // If it isn't a wall
                if (nextSpace.getPlayer() != null) { // If there is a player on the nextSpace
                    List<Player> playersToPush = new ArrayList<>();
                    Heading pushDirection = currentSpace.getDirectionToOtherSpace(nextSpace);
                    boolean couldPush = tryMovePlayerInDirection(currentSpace, pushDirection, playersToPush);
                    if (couldPush) {
                        // Handle pushing players in EventHandler
                        EventHandler.event_PlayerPush(board.getSpaces(), player, playersToPush, pushDirection, this); // WARNING: Can lead to infinite loop
                    } else {
                        // There is a wall at the end of player chain
                        couldMove = false;
                    }
                }
            } else {
                // There is a wall between currentSpace and nextSpace
                couldMove = false;
            }
        }

        if (!couldMove) {
            nextSpace = currentSpace;
        }

        // Setting the players position to nextSpace in the EventHandler
        EventHandler.event_PlayerMove(player, nextSpace, this);
    }

    /**
     * Tries to push players recursively.
     * @param space The current space being checked.
     * @param direction The direction we want to push.
     * @return A list of players being pushed.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public boolean tryMovePlayerInDirection(Space space, Heading direction, List<Player> playersToPush)  {
        Player playerOnSpace = space.getPlayer();
        Space nextSpace = space.getSpaceNextTo(direction, board.getSpaces());
        if (nextSpace == null) {                                // Base case, player fell off LULW
            playersToPush.add(playerOnSpace);
            return true;
        }
        boolean isWallBetween = space.getIsWallBetween(nextSpace);
        if (nextSpace.getPlayer() == null && !isWallBetween) {  // Base case, no player on next space and no wall between
            playersToPush.add(playerOnSpace);
            return true;
        }
        if (nextSpace.getPlayer() != null) {                    // In case more players to move
            if (tryMovePlayerInDirection(nextSpace, direction, playersToPush)) {
                // If all other players have moved, we also move.
                playersToPush.add(playerOnSpace);
                return true;
            } return false;  // If push chain was stopped by wall
        } else {                                                // In case of wall
            return false;
        }
    }

    /**
     * Method for starting the programming phase. This is needed for resetting some parameters in order to prepare for the programming phase.
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);

        board.setCurrentRegister(0);
        board.updatePriorityList();
        board.setCurrentPlayer(board.getPriorityList().peek());

        for (int i = 0; i < board.getNoOfPlayers(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                player.discardAll();
                for (int j = 0; j < Player.NO_OF_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setVisible(true);
                }
                player.drawHand();
                for (int j = 0; j < Player.NO_OF_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setVisible(true);
                }
            }
        }
    }

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * 9);
        return new CommandCard(commands[random]);
    }
    
    public void finishProgrammingPhase() {
        board.setPhase(Phase.ACTIVATION);

        board.setCurrentPlayer(board.getPriorityList().poll());
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
    }

    // XXX: implemented in the current version
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_OF_REGISTERS) {
            for (int i = 0; i < board.getNoOfPlayers(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: implemented in the current version
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getNoOfPlayers(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_OF_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: implemented in the current version
    public void executePrograms() {
        board.setStepMode(false);
        handlePlayerRegister();
    }

    // XXX: implemented in the current version
    public void executeRegister() {
        board.setStepMode(true);
        handlePlayerRegister();
    }

    /**
     *     These methods are the main flow of a register.
     */
    private void handlePlayerRegister() {
        turnPlaying = true;
        makeProgramFieldsVisible(board.getCurrentRegister());
        Player currentPlayer = board.getCurrentPlayer();
        if (!currentPlayer.getIsRebooting()) {
            // Handle the players command on the current register. This will queue any command on the register.
            handlePlayerCommand(currentPlayer);
        }
        // Begin handling the actions.
        handlePlayerActions();
    }
    /**
     * This method splits up handleNextPlayerTurn(), in order to call this again, if the command is a PLAYER_INTERACTION.
     */
    private void handlePlayerActions() {
        // Run through the queue and execute the player command.
        runActionsAndCallback(this::handleEndOfPlayerTurn);
    }

    private void handleEndOfPlayerTurn() {
        if (board.getPhase() == Phase.PLAYER_INTERACTION) {
            // Return and wait for PLAYER_INTERACTION.
            return;
        }
        // When player command is executed, check if there are more player turns this register.
        if (!board.getPriorityList().isEmpty()) {
            // There are more players in the priorityList. Continue to next player.
            // Take player from the queue
            board.setCurrentPlayer(board.getPriorityList().poll());
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
        // Queue board elements and player lasers.
        queueBoardElementsAndRobotLasers();
        handleBoardElementActions();
    }
    private void handleBoardElementActions() {
        // Execute board elements and player lasers. When actions have taken place, we go to the next register.
        runActionsAndCallback(this::nextRegister);
    }
    public void nextRegister() {
        /*for (Player player : board.getPlayers()) {
            Space playerSpace = player.getSpace();
            playerSpace.setPlayer(player);
        }*/
        int currentRegister = board.getCurrentRegister();
        if (currentRegister < Player.NO_OF_REGISTERS - 1) {
            // Set next register
            currentRegister++;
            turnPlaying = false;
            // If there are more registers, set the currentRegister and continue to the next player.
            board.setCurrentRegister(currentRegister);
            board.updatePriorityList();
            // Take player from the queue
            board.setCurrentPlayer(board.getPriorityList().poll());
            if (!board.isStepMode()) {
                handlePlayerRegister();
            }
        } else {
            handleEndOfRound();
        }
    }

    private void handleEndOfRound() {
        // If all registers are done
        turnPlaying = false;
        PauseTransition pause = new PauseTransition(Duration.millis(nextRegisterDelay));
        pause.setOnFinished(event -> {
            for (Player player : board.getPlayers()) {
                player.setIsRebooting(false);
                player.getSpace().updateSpace();
            }
            startProgrammingPhase();
        });  // Small delay before ending activation phase for dramatic effect ;-).
        pause.play();
    }

    private void runActionsAndCallback(Runnable callback) {
        if (board.getPhase() == Phase.ACTIVATION) {
            if (!actionQueue.isEmpty()) {
                // Handle the next action
                ActionWithDelay nextAction = actionQueue.removeFirst();
                nextAction.getAction(WITH_ACTION_MESSAGE).run();
                Duration delay = nextAction.getDelay();
                PauseTransition pause = new PauseTransition(delay);
                pause.setOnFinished(event -> runActionsAndCallback(callback)); // Continue actions
                if (WITH_ACTION_DELAY) {
                    pause.play();
                }
            } else { // When we have exhausted the actions, call the callback method.
                callback.run();
            }
        } else if (board.getPhase() == REBOOTING) {
            startRebootPhase();
        }
    }

    // XXX: implemented in the current version
    private void handlePlayerCommand(Player currentPlayer) {
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int currentRegister = board.getCurrentRegister();
            if (currentRegister >= 0 && currentRegister < Player.NO_OF_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(currentRegister).getCard();
                if (card != null) {
                    Command command = card.command;
                    queuePlayerCommand(currentPlayer, command);
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * Handle the player command for a command with multiple options
     *
     *  @author Michael Sylvest Bendtsen, s214954@dtu.dk
     *  @param option the option the player have chosen, and sets the activation phase active again
     */
    public void executeCommandOptionAndContinue(Command option){
        queuePlayerCommand(board.getCurrentPlayer(), option);
        board.setPhase(Phase.ACTIVATION);
        handlePlayerActions();
    }


    public void startRebootPhase() {
        board.setPhase(Phase.REBOOTING);
        handleNextReboot();
    }

    public void addPlayerToRebootQueue(Player player) {
        if (!playersRebooting.contains(player)) {
            playersRebooting.offer(player);
        }
    }

    private void handleNextReboot() {
        if (playersRebooting.isEmpty()) {
            handleEndOfReboot();
        } else {
            Player playerRebooting = playersRebooting.peek();
            if (playerRebooting.getIsRebooting()) {
                setDirectionOptionsPane(playerRebooting.getTemporarySpace());
            }
        }
    }

    private void handleEndOfReboot() {
        board.setPhase(ACTIVATION);
        handlePlayerActions();
    }



    /**
     * sets the players action from the command
     * @param player
     * @param command
     * @author Maximillian Bjørn Mortensen
     */
    public void queuePlayerCommand(@NotNull Player player, Command command) {
        if (player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).
            // Call the event handler, and let it modify the command
            command = EventHandler.event_RegisterActivate(player, command);

            switch (command) {
                case FORWARD:
                    player.setVelocity(new Velocity(1, 0));
                    startPlayerMovement(player);
                    break;
                case FAST_FORWARD:
                    player.setVelocity(new Velocity(2, 0));
                    startPlayerMovement(player);
                    break;
                case VARY_FAST_FORWARD:
                    player.setVelocity(new Velocity(3, 0));
                    startPlayerMovement(player);
                    break;
                case RIGHT:
                    turnPlayer(player, 1);
                    break;
                case LEFT:
                    turnPlayer(player, -1);
                    break;
                case U_TURN:
                    turnPlayer(player, 2);
                    break;
                case BACKWARD:
                    player.setVelocity(new Velocity(-1, 0));
                    startPlayerMovement(player);
                    break;
                case AGAIN:
                    switch (player.getLastCmd()) {
                        case DAMAGE:
                            //TODO
                            break;
                        case UPGRADE:
                            player.addEnergyCube();
                            break;
                        default:
                            queuePlayerCommand(player, player.getLastCmd());
                    }
                    break;
                case POWER_UP:
                    player.addEnergyCube();
                    break;
                case OPTION_LEFT_RIGHT:
                    board.setPhase(Phase.PLAYER_INTERACTION);
                    break;
                default:
                    // DO NOTHING (for now)
                    System.out.println("Can't find command: " + command.displayName);
                    break;
            }

            if(command != Command.AGAIN) player.setLastCmd(command);

            // After command is executed, set the next player:
            var currentPlayerIndex = board.getPlayerNumber(board.getCurrentPlayer()); // Get the index of the current player
            var nextPlayerIndex = (currentPlayerIndex + 1) % board.getNoOfPlayers(); // Get the index of the next player
            //board.setCurrentPlayer(board.getPlayer(nextPlayerIndex)); // Set the current player to the next player
            //The current move counter is set to the old movecounter+1
            board.setMoveCounter(board.getMoveCounter() + 1); // Increase the move counter by one
        }
    }

    /**
     * moves player based on heading and velocity
     * @param player
     * @author Maximillian Bjørn Mortensen
     */
    private void startPlayerMovement(Player player) {
        // We take stepwise movement, and call moveCurrentPlayerToSpace() for each.
        Velocity playerVelocity = player.getVelocity();

        // For each forward movement
        for (int i = 0; i < Math.abs(playerVelocity.forward); i++) {
            actionQueue.addFirst(new ActionWithDelay(() -> {
                Heading direction = (playerVelocity.forward > 0) ? player.getHeading() : player.getHeading().opposite();
                if (!player.getIsRebooting()) {
                    movePlayerToSpace(player, board.getNeighbour(player.getSpace(), direction));
                }
                // Decrement
                playerVelocity.forward -= (playerVelocity.forward > 0) ? 1 : -1;
            }, Duration.millis(150), "Player movement: " + player.getName()));
        }

        // For each sideways movement
        for (int i = 0; i < Math.abs(playerVelocity.right); i++) {
            actionQueue.addFirst(new ActionWithDelay(() -> {
                Heading direction = (playerVelocity.right > 0) ? player.getHeading().next() : player.getHeading().prev();
                if (!player.getIsRebooting()) {
                    movePlayerToSpace(player, board.getNeighbour(player.getSpace(), direction));
                }
                // Decrement
                playerVelocity.right -= (playerVelocity.right > 0) ? 1 : -1;
            }, Duration.millis(150), "Player movement: " + player.getName()));
        }
    }

    /**
     * sets heading for player based on paramater
     * @param player
     * @param quarterRotationClockwise
     * @author Maximillian Bjørn Mortensen
     */
    private void turnPlayer(Player player, int quarterRotationClockwise) {
        boolean rotateClockwise = quarterRotationClockwise > 0;
        for (int i = 0; i < Math.abs(quarterRotationClockwise); i++) {
            actionQueue.addFirst(new ActionWithDelay(() -> {
                Heading prevOrientation = player.getHeading();
                Heading newOrientation = rotateClockwise ? prevOrientation.next() : prevOrientation.prev();
                if (!player.getIsRebooting()) {
                    player.setHeading(newOrientation);
                }
            }, Duration.millis(150), "Player rotation: " + player.getName()));
        }
    }

    public void queueBoardElementsAndRobotLasers() {
        List<Space>[] boardElementsSpaces = board.getBoardElementsSpaces();

        // 1. Blue conveyor belts
        actionQueue.addLast(new ActionWithDelay(() -> {
            for (Space conveyorBeltSpace : boardElementsSpaces[0]) {
                conveyorBeltSpace.getBoardElement().doAction(conveyorBeltSpace, this, actionQueue);
            }
            for (int i = 0; i < board.getNoOfPlayers(); i++) {
                Player player = board.getPlayer(i);
                player.goToTemporarySpace();
            }
        }, Duration.millis(100), "Blue conveyor belts"));

        // 2. Green conveyor belts
        actionQueue.addLast(new ActionWithDelay(() -> {
            for (Space conveyorBeltSpace : boardElementsSpaces[1]) {
                conveyorBeltSpace.getBoardElement().doAction(conveyorBeltSpace, this, actionQueue);
            }
            for (int i = 0; i < board.getNoOfPlayers(); i++) {
                Player player = board.getPlayer(i);
                player.goToTemporarySpace();
            }
        }, Duration.millis(100), "Green conveyor belts"));

        // 3. Push panels
        actionQueue.addLast(new ActionWithDelay(() -> {
            for (Space pushPanel : boardElementsSpaces[2]) {
                pushPanel.getBoardElement().doAction(pushPanel, this, actionQueue);
            }
        }, Duration.millis(100), "Push panels"));

        // 4. Gears
        actionQueue.addLast(new ActionWithDelay(() -> {
            for (Space gearSpace : boardElementsSpaces[3]) {
                gearSpace.getBoardElement().doAction(gearSpace, this, actionQueue);
            }
        }, Duration.millis(100), "Gears"));

        // 5. Board lasers
        actionQueue.addLast(new ActionWithDelay(() -> { // Shooting all board lasers at the same time
            for (Space boardLaser : boardElementsSpaces[4]) {
                boardLaser.getBoardElement().doAction(boardLaser, this, actionQueue);
            }
        }, Duration.millis(150), "Board laser"));

        // 6. Robot lasers
        actionQueue.addLast(new ActionWithDelay(board::clearLasers, Duration.millis(0)));
        for (Player player : board.getPlayers()) {
            actionQueue.addLast(new ActionWithDelay(() -> {
                EventHandler.event_PlayerShoot(board.getSpaces(), player, actionQueue);
            }, Duration.millis(250), "Player laser"));
        }

        // 7. Energy spaces
        actionQueue.addLast(new ActionWithDelay(board::clearLasers, Duration.millis(0)));
        actionQueue.addLast(new ActionWithDelay(() -> {
            for (Space energySpace : boardElementsSpaces[5]) {
                energySpace.getBoardElement().doAction(energySpace, this, actionQueue);
            }
        }, Duration.millis(0), "Energy spaces"));

        // 8. Checkpoints
        actionQueue.addLast(new ActionWithDelay(() -> {
            for (Space checkpoint : boardElementsSpaces[6]) {
                checkpoint.getBoardElement().doAction(checkpoint, this, actionQueue);
            }
        }, Duration.millis(0), "Check points"));
    }





    private void setDirectionOptionsPane(Space space) {
        // Quirk fix for showing the player when another player is on the respawning players' spawn
        if (board.getPhase() == REBOOTING) {
            Player playerRebooting = playersRebooting.peek();
            assert playerRebooting != null;
            Player playerOnSpawnPoint = space.getPlayer();
            if (playerOnSpawnPoint != null) {
                if (playerOnSpawnPoint != playerRebooting) {
                    if (playerRebooting.getSpawnPoint() == space) {
                        Space otherSpawnPoint = playerOnSpawnPoint.getSpawnPoint();
                        playerOnSpawnPoint.setSpace(otherSpawnPoint);
                        playerOnSpawnPoint.setTemporarySpace(otherSpawnPoint);
                        EventHandler.event_PlayerReboot(playerOnSpawnPoint, this);
                        otherSpawnPoint.updateSpace();
                    }
                }
            }
            playerRebooting.goToTemporarySpace();
            space.updateSpace();
        }

        directionOptionsSpace = space;
        board.updateBoard();
    }

    public void spacePressed(MouseEvent event, SpaceView spaceView, Space space) {
        if (board.getPhase() == INITIALIZATION) {
            if (space.getBoardElement() instanceof BE_SpawnPoint) {
                Player currentPlayer = board.getCurrentPlayer();
                if (space.getPlayer() == null) {
                    space.setPlayer(currentPlayer);
                    setDirectionOptionsPane(space);
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
                startProgrammingPhase();
            }
        } else if (board.getPhase() == REBOOTING) {
            Player player = playersRebooting.poll();
            player.setHeading(direction);

            handleNextReboot();
        }
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if(sourceCard.command != null && sourceCard.command == Command.AGAIN && target.index == 1) return false;
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }
}
