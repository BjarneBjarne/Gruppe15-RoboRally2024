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
import gruppe15.roborally.model.boardelements.BE_Antenna;
import gruppe15.roborally.model.boardelements.BE_ConveyorBelt;
import gruppe15.roborally.model.boardelements.BE_BoardLaser;
import gruppe15.roborally.model.boardelements.BoardElement;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
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
    private final boolean WITH_ACTION_MESSAGE = true;


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
        System.out.println("Programming phase");
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setCurrentRegister(0);

        for (int i = 0; i < board.getNoOfPlayers(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_OF_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: implemented in the current version
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: implemented in the current version
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.getPriorityList().clear();
        board.getPriorityList().addAll(determineAllPriority());
        board.setCurrentPlayer(board.getPriorityList().remove(0));
        board.setCurrentRegister(0);
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
        continuePrograms();
    }

    // XXX: implemented in the current version
    public void executeRegisters() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: implemented in the current version
    private void continuePrograms() {
        if (board.getPhase() == Phase.ACTIVATION) {
            // Execute the logic for this register. This includes adding "Actions" to the queue.
            if (actionQueue.isEmpty()) {
                executeRegister();
                continueActions();
            }
        }
    }

    private void continueActions() {
        if (!actionQueue.isEmpty()) {
            // Handle the next action
            ActionWithDelay nextAction = actionQueue.removeFirst();
            nextAction.getAction(WITH_ACTION_MESSAGE).run();
            Duration delay = nextAction.getDelay();
            PauseTransition pause = new PauseTransition(delay);
            pause.setOnFinished(event -> continueActions()); // Continue programs (& actions)
            if (WITH_ACTION_DELAY) {
                pause.play();
            }
        }
    }

    public ArrayList<Player> determineAllPriority(){
        Space antenna = findAntenna();
        ArrayList<Player> priorityList = new ArrayList<>();
        for(int i = 0;i<board.getNoOfPlayers();i++){
            board.getPlayer(i).setPriority(determinePlayerPriority(board.getPlayer(i),antenna));
            priorityList.add(board.getPlayer(i));
        }
        //sorting list
        for (int i = 0;i<priorityList.size()-1;i++){
            if(priorityList.get(i).getPriority() > priorityList.get(i+1).getPriority() )
            {
                Collections.swap(priorityList,i,i+1);
                i=-1;
            }
        }
        /*for (int i =0;i<priorityList.size();i++){
            System.out.println(priorityList.get(i).getName()+": "+priorityList.get(i).getPriority());
        }*/
        //TODO: Implement real tiebreaker
        return priorityList;
    }

    public Integer determinePlayerPriority(Player player,Space antenna){
        int x = antenna.x - player.getSpace().x;
        int y = antenna.y - player.getSpace().y;
        return Math.abs(x) + Math.abs(y);
    }
    public Space findAntenna(){
        Space[][] spaces = board.getSpaces();
        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                BoardElement boardElement = spaces[x][y].getBoardElement();
                if (boardElement instanceof BE_Antenna) {
                    return  spaces[x][y];
                }
            }
        }
        System.out.println("Err: No Priority antenna found");
        return null;
    }

    // XXX: implemented in the current version
    private void executeRegister() {
        Player currentPlayer = board.getCurrentPlayer();
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
                    executeCommand(currentPlayer, command);
                }
                changeToNextRegisterAndHandleBoardElements(currentPlayer,currentRegister);
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    public void changeToNextRegisterAndHandleBoardElements(Player currentPlayer,int currentRegister){
        if (!board.getPriorityList().isEmpty()) {
            board.setCurrentPlayer(board.getPriorityList().remove(0));
        } else {
            handleBoardElements();
            currentRegister++;
            if (currentRegister < Player.NO_OF_REGISTERS) {
                makeProgramFieldsVisible(currentRegister);
                board.setCurrentRegister(currentRegister);
                board.getPriorityList().clear();
                board.getPriorityList().addAll(determineAllPriority());

                board.setCurrentPlayer(board.getPriorityList().remove(0));

            } else {
                PauseTransition pause = new PauseTransition(Duration.millis(nextRegisterDelay));
                pause.setOnFinished(event -> continuePrograms());
                pause.play();
                startProgrammingPhase();
            }
        }
    }

    public void handleBoardElements() {
        Space[][] spaces = board.getSpaces();
        // 1. Blue conveyor belts
        actionQueue.addLast(new ActionWithDelay(() -> {
            for (int i = 0; i < board.getNoOfPlayers(); i++) {
                Space playerSpace = board.getPlayer(i).getSpace();
                BoardElement boardElement = playerSpace.getBoardElement();
                if (boardElement instanceof BE_ConveyorBelt) {
                    boardElement.doAction(playerSpace, board, actionQueue);
                }
            }
        }, Duration.millis(0), "Blue conveyor belts"));
        // 2. Green conveyor belts
        actionQueue.addLast(new ActionWithDelay(() -> {
            for (int x = 0; x < spaces.length; x++) {
                for (int y = 0; y < spaces[x].length; y++) {
                    BoardElement boardElement = spaces[x][y].getBoardElement();
                    if (boardElement instanceof BE_ConveyorBelt) {
                        boardElement.doAction(spaces[x][y], board, actionQueue);
                    }
                }
            }
        }, Duration.millis(0), "Green conveyor belts"));
        // 3. Push panels
        actionQueue.addLast(new ActionWithDelay(() -> {

        }, Duration.millis(0), "Push panels"));
        // 4. Gears
        actionQueue.addLast(new ActionWithDelay(() -> {

        }, Duration.millis(0), "Gears"));
        // 5. Board lasers
        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                BoardElement boardElement = spaces[x][y].getBoardElement();
                if (boardElement instanceof BE_BoardLaser) {
                    boardElement.doAction(spaces[x][y], board, actionQueue);
                }
            }
        }
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
        }, Duration.millis(0), "Energy spaces"));
        // 8. Checkpoints
        actionQueue.addLast(new ActionWithDelay(() -> {

        }, Duration.millis(0), "Checkpoints"));
    }

    public void executeCommand(@NotNull Player player, Command command) {
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
                    case RIGHT:
                        turnPlayer(player, 1);
                        break;
                    case LEFT:
                        turnPlayer(player, -1);
                        break;
                    case OPTION_LEFT_RIGHT:
                        break;
                    default:
                        // DO NOTHING (for now)
                }

                board.setMoveCounter(board.getMoveCounter() + 1); // Increase the move counter by one
            }, Duration.millis(0), "Player movement: " + player.getName()));
        }
    }

    private void setPlayerVelocity(Player player, int fwd, int rgt) {
        // We take stepwise movement, and call moveCurrentPlayerToSpace() for each.

        // For each forward movement
        for (int i = 0; i < fwd; i++) {
            Space temp = player.getSpace();
            int x = temp.x;
            int y = temp.y;
            switch(player.getHeading()){
                case NORTH:
                    y = y - 1;
                    break;
                case SOUTH:
                    y = y + 1;
                    break;
                case EAST:
                    x = x + 1;
                    break;
                case WEST:
                    x = x - 1;
                    break;
                default:
            }
            movePlayerToSpace(player, board.getSpace(x, y));
        }

        // For each sideways movement
        for (int i = 0; i < rgt; i++) {
            Space temp = player.getSpace();
            int x = temp.x;
            int y = temp.y;
            switch(player.getHeading()){
                case NORTH:
                    x = x + 1;
                    break;
                case SOUTH:
                    x = x - 1;
                    break;
                case EAST:
                    y = y + 1;
                    break;
                case WEST:
                    y = y - 1;
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

    public void executeCommandOptionAndContinue(Command option){
        executeCommand(board.getCurrentPlayer(), option);

        Player currentPlayer = board.getCurrentPlayer();
        int currentRegister = board.getCurrentRegister();
        board.setPhase(Phase.ACTIVATION);

        changeToNextRegisterAndHandleBoardElements(currentPlayer,currentRegister);

        if(!board.isStepMode()){
        continuePrograms();
        }
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
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
