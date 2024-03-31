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
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * Takes the current player of the board and sets the players position to the given space
     * if the space is free. The current player is then set to the player following the current player.
     *
     * @autor Tobias Nicolai Frederiksen, s235086@dtu.dk
     * @param space the space to which the current player should move
     * @return void
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        // TODO Task1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free())
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if the player is moved
        if (space.getPlayer() != null) return; // do nothing if the space is already occupied

        board.getCurrentPlayer().setSpace(space); // Set the current players position to the new space
        var currentPlayerIndex = board.getPlayerNumber(board.getCurrentPlayer()); // Get the index of the current player
        var nextPlayerIndex = (currentPlayerIndex + 1) % board.getPlayersNumber(); // Get the index of the next player
        board.setCurrentPlayer(board.getPlayer(nextPlayerIndex)); // Set the current player to the next player
        board.setMoveCounter(board.getMoveCounter() + 1); // Increase the move counter by one
    }

    // XXX: implemented in the current version
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
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
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: implemented in the current version
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: implemented in the current version
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
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
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: implemented in the current version
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX: implemented in the current version
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
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

    // XXX: implemented in the current version
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    private Space corectPosition(int x, int y){
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

    private void moveCurrentPlayer(Player player, int fwd, int rgt){
        Space temp = player.getSpace();
        int x = temp.x;
        int y = temp.y;
        switch(player.getHeading()){
            case NORTH:
                y = y - fwd;
                x = x + rgt;
                break;
            case SOUTH:
                y = y + fwd;
                x = x - rgt;
                break;
            case EAST:
                x = x + fwd;
                y = y + rgt;
                break;
            case WEST:
                x = x - fwd;
                y = y -rgt;
                break;
            default:
        }

        moveCurrentPlayerToSpace(corectPosition(x, y));
    }

    private void turnCurrentPlayerClockwise(Player player, int quaterRotation){
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
        quaterRotation = quaterRotation%4;
        if( quaterRotation<0 ) quaterRotation = quaterRotation+4;
        int newOrientation = (quaterRotation+playerOrientation)%4;
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

    // TODO Task2
    public void moveForward(@NotNull Player player) {
        moveCurrentPlayer(player, 1, 0);
    }

    // TODO Task2
    public void fastForward(@NotNull Player player) {
        moveCurrentPlayer(player, 2, 0);
    }

    // TODO Task2
    public void turnRight(@NotNull Player player) {
        turnCurrentPlayerClockwise(player, 1);
    }

    // TODO Task2
    public void turnLeft(@NotNull Player player) {
        turnCurrentPlayerClockwise(player, -1);
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
