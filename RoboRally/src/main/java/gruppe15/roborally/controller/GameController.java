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
import gruppe15.roborally.model.boardelements.BE_BoardLaser;
import gruppe15.roborally.model.boardelements.BE_ConveyorBelt;
import gruppe15.roborally.model.boardelements.BE_EnergySpace;
import gruppe15.roborally.model.boardelements.BoardElement;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    public final Board board;
    private final LinkedList<ActionWithDelay> actionQueue = new LinkedList<>();
    private final int nextRegisterDelay = 1000; // In milliseconds.
    private final boolean WITH_ACTION_DELAY = true;
    private final boolean WITH_ACTION_MESSAGE = false;
    private boolean turnPlaying = false;
    public boolean getIsTurnPlaying() {
        return turnPlaying;
    }


    public GameController(@NotNull Board board) {
        this.board = board;
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
            return;
        }
        if (nextSpace == null) {
            // TODO: Make player (fall off / reboot)
            System.out.println("Next space null! Player " + player.getName() + " should fall off.");
            // Should call: EventHandler.event_PlayerReboot(player);
            return;
        }
        boolean isWallBetween = currentSpace.getIsWallBetween(nextSpace);
        boolean couldMove = false;
        if (nextSpace.getPlayer() == null && !isWallBetween) {
            couldMove = true;
        } else if (!isWallBetween) { // If it isn't a wall, try push players
            List<Player> playersToPush = new ArrayList<>();
            Heading pushDirection = currentSpace.getDirectionToOtherSpace(nextSpace);
            boolean couldPush = tryMovePlayerInDirection(currentSpace, pushDirection, playersToPush);
            if (couldPush) {
                // Handle pushing players in EventHandler
                EventHandler.event_PlayerPush(board.getSpaces(), player, playersToPush, pushDirection);
                couldMove = true;
            } else {
                // There is a wall at the end of player chain
            }
        } else {
            // There is a wall between currentSpace and nextSpace
        }

        if (couldMove) {
            // Setting the current players position to the new space in the EventHandler
            EventHandler.event_PlayerMove(player, nextSpace);
        }
    }

    /**
     * Tries to push players recursively.
     * @param space The current space being checked.
     * @param direction The direction we want to push.
     * @return A list of players being pushed.
     */
    public boolean tryMovePlayerInDirection(Space space, Heading direction, List<Player> playersToPush)  {

        Player playerOnSpace = space.getPlayer();
        Space nextSpace = space.getSpaceNextTo(direction, board.getSpaces());

        if (nextSpace == null) {                                // Base case, player fell off monkaW
            System.out.println("Next space null! Player " + (playerOnSpace == null ? "no_player" : playerOnSpace.getName()) + " should fall off.");
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

    // XXX: implemented in the current version
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
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: implemented in the current version
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * 9);
        return new CommandCard(commands[random]);
    }

    // XXX: implemented in the current version
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
    public void executeNextPlayerTurn() {
        board.setStepMode(true);
        handlePlayerRegister();
    }

    /**
     *     These methods are the main flow of a register.
     */
    private void handlePlayerRegister() {
        turnPlaying = true;
        // Handle the players command on the current register. This will queue any command on the register.
        handlePlayerCommand(board.getCurrentPlayer());
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
            turnPlaying = false;
            // There are more players in the priorityList. Continue to next player.
            // Take player from the queue
            board.setCurrentPlayer(board.getPriorityList().poll());
            handlePlayerRegister();
        } else {
            turnPlaying = false;
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
        // Execute board elements and player lasers. When actions have taken place, we go to the next register.
        runActionsAndCallback(this::nextRegister);
    }
    public void nextRegister() {
        int currentRegister = board.getCurrentRegister();
        // Set next register
        currentRegister++;
        if (currentRegister < Player.NO_OF_REGISTERS) {
            turnPlaying = false;
            // If there are more registers, set the currentRegister and continue to the next player.
            makeProgramFieldsVisible(currentRegister);
            board.setCurrentRegister(currentRegister);
            board.updatePriorityList();
            // Take player from the queue
            board.setCurrentPlayer(board.getPriorityList().poll());
            if (!board.isStepMode()) {
                handlePlayerRegister();
            }
        } else {
            // If all registers are done, we start the programming phase. TODO: When the Upgrade Phase is implemented, we should go to the Upgrade Phase here.
            PauseTransition pause = new PauseTransition(Duration.millis(nextRegisterDelay));
            pause.setOnFinished(event -> startProgrammingPhase());  // Small delay before ending activation phase for dramatic effect ;-).
            pause.play();
        }
    }
    private void runActionsAndCallback(Runnable callback) {
        if (!actionQueue.isEmpty() && board.getPhase() == Phase.ACTIVATION) {
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
    }

    // XXX: implemented in the current version
    private void handlePlayerCommand(Player currentPlayer) {
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int currentRegister = board.getCurrentRegister();
            if (currentRegister >= 0 && currentRegister < Player.NO_OF_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(currentRegister).getCard();
                if (card != null) {
                    Command command = card.command;
                    if(card.command.isInteractive()){
                        //System.out.println("check");
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
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

    public void executeCommandOptionAndContinue(Command option){
        queuePlayerCommand(board.getCurrentPlayer(), option);
        board.setPhase(Phase.ACTIVATION);
        handlePlayerActions();
    }

    public void queuePlayerCommand(@NotNull Player player, Command command) {
        if (player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).
            // Call the event handler, and let it modify the command
            command = EventHandler.event_RegisterActivate(player, command);
            Command finalCommand = command;

            actionQueue.addFirst(new ActionWithDelay(() -> {
                switch (finalCommand) {
                    case FORWARD:
                        setPlayerVelocity(player, 1, 0);
                        break;
                    case FAST_FORWARD:
                        setPlayerVelocity(player, 2, 0);
                        break;
                    case VARY_FAST_FORWARD:
                        setPlayerVelocity(player, 3, 0);
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
                        setPlayerVelocity(player, -1, 0);
                        break;
                    case AGAIN:
                        switch (player.getLastCmd()){
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
                        //TODO
                        break;
                    case OPTION_LEFT_RIGHT:
                        break;
                    default:
                        // DO NOTHING (for now)
                        System.out.println("Can't find command: " + finalCommand.displayName);
                        break;
                }

                if(finalCommand != Command.AGAIN) player.setLastCmd(finalCommand);

                // After command is executed, set the next player:
                var currentPlayerIndex = board.getPlayerNumber(board.getCurrentPlayer()); // Get the index of the current player
                var nextPlayerIndex = (currentPlayerIndex + 1) % board.getNoOfPlayers(); // Get the index of the next player
                //board.setCurrentPlayer(board.getPlayer(nextPlayerIndex)); // Set the current player to the next player
                //The current move counter is set to the old movecounter+1
                board.setMoveCounter(board.getMoveCounter() + 1); // Increase the move counter by one
            }, Duration.millis(250), "Player movement: " + player.getName()));
        }
    }

    private void setPlayerVelocity(Player player, int fwd, int rgt) {
        // We take stepwise movement, and call moveCurrentPlayerToSpace() for each.

        int move = 1;
        boolean negative = (fwd<0);
        if(negative){
            fwd = -fwd;
            move = -1;
        }

        // For each forward movement
        for (int i = 0; i < fwd; i++) {
            Space temp = player.getSpace();
            int x = temp.x;
            int y = temp.y;
            switch(player.getHeading()){
                case NORTH:
                    y = y - move;
                    break;
                case SOUTH:
                    y = y + move;
                    break;
                case EAST:
                    x = x + move;
                    break;
                case WEST:
                    x = x - move;
                    break;
                default:
            }
            movePlayerToSpace(player, board.getSpace(x, y));
        }

        move = 1;
        negative = (rgt<0);
        if(negative){
            rgt = -rgt;
            move = -1;
        }

        // For each sideways movement
        for (int i = 0; i < rgt; i++) {
            Space temp = player.getSpace();
            int x = temp.x;
            int y = temp.y;
            switch(player.getHeading()){
                case NORTH:
                    x = x + move;
                    break;
                case SOUTH:
                    x = x - move;
                    break;
                case EAST:
                    y = y + move;
                    break;
                case WEST:
                    y = y - move;
                    break;
                default:
            }
            movePlayerToSpace(player, board.getSpace(x, y));
        }
    }

    private void turnPlayer(Player player, int quaterRotationClockwise){
        int playerOrientation = 0;
        switch(player.getHeading()){
            case SOUTH:
                playerOrientation = 2;
                break;
            case EAST:
                playerOrientation = 1;
                break;
            case WEST:
                playerOrientation = 3;
                break;
            default:
        }
        quaterRotationClockwise = quaterRotationClockwise%4;
        if( quaterRotationClockwise<0 ) quaterRotationClockwise = quaterRotationClockwise+4;
        int newOrientation = (quaterRotationClockwise+playerOrientation)%4;
        switch (newOrientation) {
            case 0:
                player.setHeading(Heading.NORTH);
                break;
            case 1:
                player.setHeading(Heading.EAST);
                break;
            case 2:
                player.setHeading(Heading.SOUTH);
                break;
            case 3:
                player.setHeading(Heading.WEST);
                break;
            default:
        }
    }

    public void queueBoardElementsAndRobotLasers() {
        Space[][] spaces = board.getSpaces();

        // First we gather some information about the board.
        List<Space> greenConveyorBeltSpaces = new ArrayList<>();
        List<Space> blueConveyorBeltSpaces = new ArrayList<>();
        List<Space> energySpaces = new ArrayList<>();
        for (int i = 0; i < board.getNoOfPlayers(); i++) {
            Space playerSpace = board.getPlayer(i).getSpace();
            BoardElement boardElement = playerSpace.getBoardElement();
            if (boardElement instanceof BE_ConveyorBelt conveyorBelt) {
                if (conveyorBelt.getStrength() == 1) {
                    greenConveyorBeltSpaces.add(playerSpace);
                } else {
                    blueConveyorBeltSpaces.add(playerSpace);
                }
            } else if (boardElement instanceof BE_EnergySpace) {
                energySpaces.add(playerSpace);
            }
        }

        // 1. Blue conveyor belts
        actionQueue.addLast(new ActionWithDelay(() -> {
            for (Space conveyorBeltSpace : blueConveyorBeltSpaces) {
                conveyorBeltSpace.getBoardElement().doAction(conveyorBeltSpace, board, actionQueue);
            }
            for (int i = 0; i < board.getNoOfPlayers(); i++) {
                Player player = board.getPlayer(i);
                player.goToTemporarySpace();
            }
        }, Duration.millis(250), "Blue conveyor belts"));

        // 2. Green conveyor belts
        actionQueue.addLast(new ActionWithDelay(() -> {
            for (Space conveyorBeltSpace : greenConveyorBeltSpaces) {
                conveyorBeltSpace.getBoardElement().doAction(conveyorBeltSpace, board, actionQueue);
            }
            for (int i = 0; i < board.getNoOfPlayers(); i++) {
                Player player = board.getPlayer(i);
                player.goToTemporarySpace();
            }
        }, Duration.millis(250), "Green conveyor belts"));

        // 3. Push panels
        actionQueue.addLast(new ActionWithDelay(() -> {
            // TODO: Implement Push panels
        }, Duration.millis(0), ""));
        // 4. Gears
        actionQueue.addLast(new ActionWithDelay(() -> {
            // TODO: Implement Gears
        }, Duration.millis(0), ""));

        // 5. Board lasers
        actionQueue.addLast(new ActionWithDelay(() -> { // Shooting all board lasers at the same time
            for (int x = 0; x < spaces.length; x++) {
                for (int y = 0; y < spaces[x].length; y++) {
                    BoardElement boardElement = spaces[x][y].getBoardElement();
                    if (boardElement instanceof BE_BoardLaser) {
                        boardElement.doAction(spaces[x][y], board, actionQueue);
                    }
                }
            }
        }, Duration.millis(250), "Board laser"));

        // Clearing the last board laser.
        actionQueue.addLast(new ActionWithDelay(() -> {
            for (int x = 0; x < spaces.length; x++) {
                for (int y = 0; y < spaces[x].length; y++) {
                    spaces[x][y].clearLasersOnSpace();
                }
            }
        }, Duration.millis(0)));

        // 6. Robot lasers
        for (int i = 0; i < board.getNoOfPlayers(); i++) {
            EventHandler.event_PlayerShoot(board.getSpaces(), board.getPlayer(i), actionQueue);
        }
        // Clearing the last robots laser.
        actionQueue.addLast(new ActionWithDelay(() -> {
            for (int x = 0; x < spaces.length; x++) {
                for (int y = 0; y < spaces[x].length; y++) {
                    spaces[x][y].clearLasersOnSpace();
                }
            }
        }, Duration.millis(0)));

        // 7. Energy spaces
        actionQueue.addLast(new ActionWithDelay(() -> {
            for (Space energySpace : energySpaces) {
                energySpace.getBoardElement().doAction(energySpace, board, actionQueue);
            }
        }, Duration.millis(250), "Energy spaces"));
        // 8. Checkpoints
        actionQueue.addLast(new ActionWithDelay(() -> {
            // TODO: Implement Checkpoints
        }, Duration.millis(0), ""));
    }

    private Space correctPosition(int x, int y){
        // TODO: Instead of keeping the player inside the make them reboot
        if( x < 0 ){
            x = 0;
        }
        if(x >= board.width){
            x = board.width-1;
        }
        if( y < 0 ){
            y = 0;
        }
        if(y >= board.height){
            y = board.height-1;
        }
        return board.getSpace(x, y);
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
