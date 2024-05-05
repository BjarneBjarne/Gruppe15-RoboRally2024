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
import gruppe15.roborally.model.boardelements.*;
import gruppe15.roborally.model.events.PhaseChangeListener;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static gruppe15.roborally.model.Heading.*;
import static gruppe15.roborally.model.Heading.WEST;
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

    private final Queue<Player> priorityList = new ArrayDeque<>();


    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];

        // Setup spaces
        if (boardName.equals("defaultboard")) {

        } else if (boardName.equals("dizzy_highway")) {
            // BE_Antenna
            addSpace(0, 4, new BE_Antenna(), spaces);
            // BE_SpawnPoint points
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
                addSpace(x, y, new BE_SpawnPoint(), spaces);
            }

            // Energy spaces
            addSpace(12, 0, new BE_EnergySpace(), spaces);
            addSpace(5, 2, new BE_EnergySpace(), spaces);
            addSpace(8, 4, new BE_EnergySpace(), spaces);
            addSpace(7, 5, new BE_EnergySpace(), spaces);
            addSpace(10, 7, new BE_EnergySpace(), spaces);
            addSpace(3, 9, new BE_EnergySpace(), spaces);

            // Conveyor belts
            addSpace(2, 0, new BE_ConveyorBelt(EAST, 1), spaces);
            addSpace(2, 9, new BE_ConveyorBelt(EAST, 1), spaces);

            for (int i = 3; i <= 10; i++) {
                addSpace(i, 8, new BE_ConveyorBelt(EAST, 2), spaces);
            }
            for (int i = 2; i <= 9; i++) {
                addSpace(11, i, new BE_ConveyorBelt(NORTH, 2), spaces);
            }
            for (int i = 5; i <= 12; i++) {
                addSpace(i, 1, new BE_ConveyorBelt(WEST, 2), spaces);
            }
            for (int i = 0; i <= 7; i++) {
                addSpace(4, i, new BE_ConveyorBelt(SOUTH, 2), spaces);
            }
            addSpace(3, 7, new BE_ConveyorBelt(EAST, 2), spaces);
            addSpace(10, 9, new BE_ConveyorBelt(NORTH, 2), spaces);
            addSpace(12, 2, new BE_ConveyorBelt(WEST, 2), spaces);
            addSpace(5, 0, new BE_ConveyorBelt(SOUTH, 2), spaces);

            // Board lasers
            addSpace(6, 4, new BE_BoardLaser(NORTH), spaces);
            addSpace(9, 5, new BE_BoardLaser(SOUTH), spaces);
            addSpace(8, 3, new BE_BoardLaser(EAST), spaces);
            addSpace(7, 6, new BE_BoardLaser(WEST), spaces);

            // Walls
            //Heading[][] walls = new Heading[this.width][this.height];
            List<Heading>[][] walls = new ArrayList[this.width][this.height];
            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    walls[x][y] = new ArrayList<>();
                }
            }
            walls[1][2].add(NORTH);
            walls[6][3].add(NORTH);
            walls[9][5].add(NORTH);
            walls[1][7].add(SOUTH);
            walls[6][4].add(SOUTH);
            walls[9][6].add(SOUTH);
            walls[2][4].add(EAST);
            walls[2][5].add(EAST);
            walls[7][6].add(EAST);
            walls[9][3].add(EAST);
            walls[6][6].add(WEST);
            walls[8][3].add(WEST);

            // Fill the rest of the spaces with empty spaces and set background images
            Image backgroundStart = ImageUtils.getImageFromName("emptyStart.png");
            Image background = ImageUtils.getImageFromName("empty.png");
            for (int x = 0; x < width; x++) {
                for(int y = 0; y < height; y++) {
                    // Add empty space
                    if (spaces[x][y] == null) {
                        addSpace(x, y, null, spaces);
                    }
                    // Set background image
                    if (x < 3) {
                        spaces[x][y].setBackgroundImage(backgroundStart);
                    } else {
                        spaces[x][y].setBackgroundImage(background);
                    }
                    // Add walls if any
                    for (Heading wall : walls[x][y]) {
                        spaces[x][y].addWall(wall);
                    }
                }
            }
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (spaces[x][y].getBoardElement() instanceof BE_ConveyorBelt) {
                        spaces[x][y].getBoardElement().calculateImage(x, y, spaces);
                    }
                }
            }
        }

        this.stepMode = false;
    }

    private void addSpace(int x, int y, BoardElement boardElement, Space[][] spaces) {
        spaces[x][y] = new Space(this, x, y, boardElement);
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

    public Queue<Player> getPriorityList() {
       return priorityList;
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
        }
        notifyChange();
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

    public void updatePriorityList() {
        priorityList.clear();
        Space antenna = findAntenna();
        ArrayList<Player> newPriorityList = new ArrayList<>();
        for(int i = 0; i < getNoOfPlayers(); i++){
            Player player = getPlayer(i);
            player.setPriority(determinePlayerPriority(player, antenna));
            newPriorityList.add(player);
        }
        //sorting list
        for (int i = 0; i < newPriorityList.size() - 1; i++){
            if(newPriorityList.get(i).getPriority() > newPriorityList.get(i + 1).getPriority()) {
                Collections.swap(newPriorityList, i, i + 1);
                i = -1;
            }
        }
        /*for (int i =0;i<priorityList.size();i++){
            System.out.println(priorityList.get(i).getName()+": "+priorityList.get(i).getPriority());
        }*/
        //TODO: Implement real tiebreaker
        priorityList.addAll(newPriorityList);
    }

    public Integer determinePlayerPriority(Player player,Space antenna) {
        int x = antenna.x - player.getSpace().x;
        int y = antenna.y - player.getSpace().y;
        return Math.abs(x) + Math.abs(y);
    }
    public Space findAntenna() {
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
}
