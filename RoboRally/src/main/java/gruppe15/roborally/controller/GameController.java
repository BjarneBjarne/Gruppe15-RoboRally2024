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
import gruppe15.roborally.model.upgrades.UpgradeCard;
import gruppe15.roborally.model.upgrades.UpgradeCardPermanent;
import gruppe15.roborally.model.upgrades.UpgradeCardTemporary;
import gruppe15.roborally.view.BoardView;
import gruppe15.roborally.view.SpaceView;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static gruppe15.roborally.model.CardField.CardFieldTypes.*;
import static gruppe15.roborally.model.Phase.*;

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
    private boolean isRegisterPlaying = false;

    // Actions
    private final LinkedList<ActionWithDelay> actionQueue = new LinkedList<>();
    private final int nextRegisterDelay = 1000; // In milliseconds.
    private final boolean WITH_ACTION_DELAY = true;
    private final boolean WITH_ACTION_MESSAGE = false;

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
                player.discardAll();
                for (int j = 0; j < Player.NO_OF_REGISTERS; j++) {
                    CardField field = player.getProgramField(j);
                    field.setVisible(true);
                }
                player.drawHand();
                for (int j = 0; j < Player.NO_OF_CARDS; j++) {
                    CardField field = player.getCardField(j);
                    field.setVisible(true);
                }
            }
        }
    }

    public void finishProgrammingPhase() {
        board.setPhase(ACTIVATION);
        board.updateBoard();

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
        runActionsAndCallback(this::handleEndOfPlayerTurn);
    }

    private void handleEndOfPlayerTurn() {
        if (board.getPhase() == PLAYER_INTERACTION) {
            // Return and wait for PLAYER_INTERACTION.
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
        // Queue board elements and player lasers.
        queueBoardElementsAndRobotLasers();
        handleBoardElementActions();
    }
    private void handleBoardElementActions() {
        // Execute board elements and player lasers. When actions have taken place, we go to the next register.
        runActionsAndCallback(this::nextRegister);
    }
    public void nextRegister() {
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
        PauseTransition pause = new PauseTransition(Duration.millis(nextRegisterDelay));
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

    private void runActionsAndCallback(Runnable callback) {
        if (board.getPhase() == ACTIVATION) {
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

    private void queuePlayerCommandFromCommandCard(Player currentPlayer) {
        Command commandToQueue = null;
        try {
            int currentRegister = board.getCurrentRegister();
            CommandCard card = (CommandCard) currentPlayer.getProgramField(currentRegister).getCard();
            commandToQueue = card.command;
            queuePlayerCommand(currentPlayer, commandToQueue);
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
    public void executeCommandOptionAndContinue(Command option){
        queuePlayerCommand(board.getCurrentPlayer(), option, true);
        board.setPhase(ACTIVATION);
        handlePlayerActions();
    }


    public void startRebootPhase() {
        board.setPhase(REBOOTING);
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

    public void queuePlayerCommand(@NotNull Player player, Command command) {
        queuePlayerCommand(player, command, false);
    }

    /**
     * sets the players action from the command
     * @param player
     * @param command
     * @author Maximillian Bjørn Mortensen
     */
    public void queuePlayerCommand(@NotNull Player player, Command command, boolean isOption) {
        if (player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).
            // Call the event handler, and let it modify the command

            if (!isOption) {
                command = EventHandler.event_RegisterActivate(player, command);
            }

            switch (command) {
                case MOVE_1:
                    player.setVelocity(new Velocity(1, 0));
                    startPlayerMovement(player);
                    break;
                case MOVE_2:
                    player.setVelocity(new Velocity(2, 0));
                    startPlayerMovement(player);
                    break;
                case MOVE_3:
                    player.setVelocity(new Velocity(3, 0));
                    startPlayerMovement(player);
                    break;
                case RIGHT_TURN:
                    turnPlayer(player, 1);
                    break;
                case LEFT_TURN:
                    turnPlayer(player, -1);
                    break;
                case U_TURN:
                    turnPlayer(player, 2);
                    break;
                case MOVE_BACK:
                    player.setVelocity(new Velocity(-1, 0));
                    startPlayerMovement(player);
                    break;
                case AGAIN:
                    queuePlayerCommand(player, player.getLastCmd());
                    break;
                case POWER_UP:
                    player.addEnergyCube();
                    break;
                case SPAM:
                    actionQueue.addFirst(new ActionWithDelay(() -> {
                        CommandCard topCard = player.drawFromDeck();
                        queuePlayerCommand(player, topCard.getCommand());
                    }, Duration.millis(150), "{" + player.getName() + "} activated: (" + command.displayName + ") damage."));
                    break;
                case TROJAN_HORSE:
                    actionQueue.addFirst(new ActionWithDelay(() -> {
                        for (int i = 0; i < 2; i++) {
                            player.discard(new CommandCard(Command.SPAM));
                        }
                        CommandCard topCard = player.drawFromDeck();
                        queuePlayerCommand(player, topCard.getCommand());
                    }, Duration.millis(150), "{" + player.getName() + "} activated: (" + command.displayName + ") damage."));
                    break;
                case WORM:
                    actionQueue.addFirst(new ActionWithDelay(() -> {
                        EventHandler.event_PlayerReboot(player, this);
                    }, Duration.millis(150), "{" + player.getName() + "} activated: (" + command.displayName + ") damage."));
                    break;
                case VIRUS:
                    actionQueue.addFirst(new ActionWithDelay(() -> {
                        for (Player foundPlayer : board.getPlayers()) {
                            if (player.getSpace().getDistanceFromOtherSpace(foundPlayer.getSpace()) <= 6) {
                                foundPlayer.discard(new CommandCard(Command.VIRUS));
                                foundPlayer.discard(new CommandCard(Command.SPAM));
                            }
                        }
                        CommandCard topCard = player.drawFromDeck();
                        queuePlayerCommand(player, topCard.getCommand());
                    }, Duration.millis(150), "{" + player.getName() + "} activated: (" + command.displayName + ") damage."));
                    break;
                default:
                    if (command.isInteractive()) {
                        board.setPhase(PLAYER_INTERACTION);
                        player.setCurrentOptions(command.getOptions());
                        board.updateBoard();
                    } else {
                        System.out.println("Can't find command: " + command.displayName);
                    }
                    break;
            }

            Set<Command> commandsToNotRepeat = Set.of(
                    Command.AGAIN,
                    Command.SPAM,
                    Command.TROJAN_HORSE,
                    Command.WORM,
                    Command.VIRUS
            );
            if (!commandsToNotRepeat.contains(command) && !isOption) {
                player.setLastCmd(command);
            }

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
                // Decrement
                playerVelocity.forward -= (playerVelocity.forward > 0) ? 1 : -1;
                if (!player.getIsRebooting()) {
                    movePlayerToSpace(player, board.getNeighbour(player.getSpace(), direction));
                }
            }, Duration.millis(150), "Player movement: " + player.getName()));
        }

        // For each sideways movement
        for (int i = 0; i < Math.abs(playerVelocity.right); i++) {
            actionQueue.addFirst(new ActionWithDelay(() -> {
                Heading direction = (playerVelocity.right > 0) ? player.getHeading().next() : player.getHeading().prev();
                // Decrement
                playerVelocity.right -= (playerVelocity.right > 0) ? 1 : -1;
                if (!player.getIsRebooting()) {
                    movePlayerToSpace(player, board.getNeighbour(player.getSpace(), direction));
                }
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
                EventHandler.event_PlayerShoot(board, player, actionQueue);
            }, Duration.millis(150), "Player laser"));
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
                beginGame();
            }
        } else if (board.getPhase() == REBOOTING) {
            Player player = playersRebooting.poll();
            player.setHeading(direction);

            handleNextReboot();
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
            boolean canBuyUpgradeCard = player.attemptUpgradeCardPurchase(sourceField);
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

    public void checkpointReached(Player player, int number) {
        if(number >= board.getNumberOfCheckpoints()){
            setWinner(player.getName(), player.getCharImage());
        }
    }
}
