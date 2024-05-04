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
package gruppe15.roborally.model;

import gruppe15.observer.Subject;
import gruppe15.roborally.model.boardelements.Antenna;
import gruppe15.roborally.model.boardelements.ConveyorBelt;
import gruppe15.roborally.model.boardelements.SpawnPoint;
import gruppe15.roborally.model.events.PhaseChangeListener;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static gruppe15.roborally.model.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int currentRegister = 0;
    //The counter for how many moves have been made
    private int moveCounter =0;

    private boolean stepMode;

    private ArrayList<Player> priorityList = new ArrayList<>();



    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];

        // Setup spaces
        if (boardName.equals("defaultboard")) {

        } else if (boardName.equals("dizzy_highway")) {

            // Start board spaces
            Image backgroundStart = ImageUtils.getImageFromName("emptyStart.png");
            // Antenna
            spaces[0][4] = new Space(this, 0, 4, new Antenna(), backgroundStart, null);
            // SpawnPoint points
            Point2D[] startFieldPoints = {
                    new Point2D(1,1),
                    new Point2D(0, 3),
                    new Point2D(1, 4),
                    new Point2D(1, 5),
                    new Point2D(0, 6),
                    new Point2D(1, 8)
            };
            for (Point2D startFieldPoint : startFieldPoints) {
                int x = (int) startFieldPoint.getX();
                int y = (int) startFieldPoint.getY();
                spaces[x][y] = new Space(this, x, y, new SpawnPoint(), backgroundStart, null);
            }
            // Fill the rest with empty spaces
            for (int x = 0; x < 3; x++) {
                for(int y = 0; y < height; y++) {
                    if (spaces[x][y] != null) {
                        continue;
                    }
                    spaces[x][y] = new Space(this, x, y, null, backgroundStart, null);
                }
            }


            // Main board
            Image background = ImageUtils.getImageFromName("empty.png");
            // Conveyor belts
            for (int x = 3; x < width; x++) {
                spaces[x][3] = new Space(this, x, 3, new ConveyorBelt(Heading.WEST), background, null);
            }
            // Fill the rest of the board with empty spaces
            for (int x = 3; x < width; x++) {
                for(int y = 0; y < height; y++) {
                    if (spaces[x][y] != null) {
                        continue;
                    }
                    spaces[x][y] = new Space(this, x, y, null, background, null);
                }
            }
        }

        this.stepMode = false;
    }

    public Board(int width, int height) {
        //this(width, height, "defaultboard");
        this(width, height, "dizzy_highway");
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public Space[][] getSpaces() {
        return spaces;
    }

    public int getNoOfPlayers() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }


    public ArrayList<Player> getPriorityList() {
       return priorityList;
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    List<PhaseChangeListener> phaseChangeListeners = new ArrayList<>();
    public void setOnPhaseChange(PhaseChangeListener listener) {
        phaseChangeListeners.add(listener);
    }
    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
            phaseChangeListeners.forEach(listener -> listener.onPhaseChange(phase));
        }
    }

    public int getCurrentRegister() {
        return currentRegister;
    }

    public void setCurrentRegister(int currentRegister) {
        if (currentRegister != this.currentRegister) {
            this.currentRegister = currentRegister;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }
    //A function to change the value of the movecounter, it also calls an update so the changes will be displayed
    public void setMoveCounter(int newMoveCounter){

        if (moveCounter != newMoveCounter) {
            moveCounter = newMoveCounter;
            notifyChange();
        }

    }


    //A public function to get the movecounter
    public int getMoveCounter(){
        return moveCounter;
    }



    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                if (y + 1 > space.board.height - 1)
                    return null;
                y = (y + 1) % height;
                break;
            case WEST:
                if (x - 1 < 0)
                    return null;
                x = (x + width - 1) % width;
                break;
            case NORTH:
                if (y - 1 < 0)
                    return null;
                y = (y + height - 1) % height;
                break;
            case EAST:
                if (x + 1 > space.board.width - 1)
                    return null;
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }

    public String getStatusMessage() {
        // This is actually a view aspect, but for making the first task easy for
        // the students, this method gives a string representation of the current
        // status of the game (specifically, it shows the phase, the player and the step)

        // TODO Task1: this string could eventually be refined
        //      The status line should show more information based on
        //      situation; for now, introduce a counter to the Board,
        //      which is counted up every time a player makes a move; the
        //      status line should show the current player and the number
        //      of the current move!

//We have added the MoveCount + getMoveCounter() to the string so it will be displayed at the bottom getMoveCounter() is a getter that gets the current move counter
        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Step: " + getCurrentRegister() +", MoveCount: "+ getMoveCounter();

        // TODO Task1: add a counter along with a getter and a setter, so the
        //      state of the board (game) contains the number of moves, which then can
        //      be used to extend the status message
    }


}
