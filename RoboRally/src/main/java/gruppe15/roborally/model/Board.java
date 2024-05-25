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
import gruppe15.roborally.model.damage.DamageTypes;
import gruppe15.roborally.model.events.PhaseChangeListener;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static gruppe15.roborally.model.Phase.INITIALIZATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {
    final public static int NO_OF_CHECKPOINTS = 6;

    public final int width;

    public final int height;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALIZATION;

    private int currentRegister = 0;
    //The counter for how many moves have been made
    private int moveCounter = 0;

    private boolean stepMode = false;

    private final Queue<Player> priorityList = new ArrayDeque<>();
    private List<Space[][]> subBoards;
    private int numberOfCheckPoints;
    private UpgradeShop upgradeShop;

    public Board(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        this.spaces = new Space[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Add empty space
                if (spaces[x][y] == null) {
                    addSpace(x, y, null, spaces);
                }
            }
        }
    }

    public Board(Pair<List<Space[][]>, Space[][]> courseSpaces) {
        this(courseSpaces.getKey(), courseSpaces.getValue());
    }
    public Board(List<Space[][]> subBoards, Space[][] spaces) {
        this.subBoards = subBoards;
        this.spaces = spaces;
        this.width = spaces.length;
        this.height = spaces[0].length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (spaces[x][y] != null) {
                    Space space = spaces[x][y];
                    space.setBoard(this);
                }
            }
        }

        updateBoardElementSpaces();
    }

    public void initializeUpgradeShop() {
        this.upgradeShop = new UpgradeShop(this);
    }

    public UpgradeShop getUpgradeShop() {
        return upgradeShop;
    }

    private void addSpace(int x, int y, BoardElement boardElement, Space[][] spaces) {
        spaces[x][y] = new Space(this, x, y, boardElement);
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

    public void updateBoard() {
        notifyChange();
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

    public List<Player> getPlayers() {
        return players;
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
        if (player == null)
            return -1; 
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
     * @param space the space for which the neighbour should be computed
     * @param direction the heading of the neighbouring space
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading direction) {
        return switch (direction) {
            case SOUTH -> {
                if (space.y + 1 >= spaces[space.x].length) {
                    yield null;
                }
                yield spaces[space.x][space.y + 1]; // out of bounds
            }
            case WEST -> {
                if (space.x - 1 < 0) {
                    yield null;
                }
                yield spaces[space.x - 1][space.y]; // out of bounds
            }
            case NORTH -> {
                if (space.y - 1 < 0) {
                    yield null;
                }
                yield spaces[space.x][space.y - 1]; // out of bounds
            }
            case EAST -> {
                if (space.x + 1 >= spaces.length) {
                    yield null;
                }
                yield spaces[space.x + 1][space.y]; // out of bounds
            }
        };
    }

    public String getStatusMessage() {
        // This is actually a view aspect, but for making the first task easy for
        // the students, this method gives a string representation of the current
        // status of the game (specifically, it shows the phase, the player and the step)

//We have added the MoveCount + getMoveCounter() to the string so it will be displayed at the bottom getMoveCounter() is a getter that gets the current move counter
        AtomicInteger noOfDamageCards = new AtomicInteger();

        for (CommandCard commandCard : getCurrentPlayer().getProgrammingDeck()) {
            if (commandCard == null) {
                continue;
            }
            for (DamageTypes damageType : DamageTypes.values()) {
                if (damageType.getCommandCardType() == commandCard.command) {
                    noOfDamageCards.getAndIncrement();
                }
            }
        }

        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Player damage cards = " + noOfDamageCards +
                ", Register: " + getCurrentRegister() + ", MoveCount: " + getMoveCounter();// +
                //",  ";
    }

    /*
        Creating an array that holds "a list of spaces" for each different board element.
        This way, we only calculate once, where the different board elements are on the board,
            for when we execute board elements actions.
    */
    private List<Space>[] boardElementsSpaces;
    private void updateBoardElementSpaces() {
        boardElementsSpaces = new List[7];
        for (int i = 0; i < boardElementsSpaces.length; i++) {
            boardElementsSpaces[i] = new ArrayList<>();
        }
        for (Space[] spaceColumn : spaces) {
            for (Space space : spaceColumn) {
                if (space == null) continue;
                BoardElement boardElement = space.getBoardElement();
                if (boardElement instanceof BE_ConveyorBelt conveyorBelt) {
                    if (conveyorBelt.getStrength() == 2) {
                        boardElementsSpaces[0].add(space);
                    } else {
                        boardElementsSpaces[1].add(space);
                    }
                } else if (boardElement instanceof BE_PushPanel) {
                    boardElementsSpaces[2].add(space);
                } else if (boardElement instanceof BE_Gear) {
                    boardElementsSpaces[3].add(space);
                } else if (boardElement instanceof BE_BoardLaser) {
                    boardElementsSpaces[4].add(space);
                } else if (boardElement instanceof BE_EnergySpace) {
                    boardElementsSpaces[5].add(space);
                } else if (boardElement instanceof BE_Checkpoint checkpoint) {
                    if (checkpoint.number > numberOfCheckPoints) numberOfCheckPoints = checkpoint.number;
                    boardElementsSpaces[6].add(space);
                }
            }
        }
    }
    public List<Space>[] getBoardElementsSpaces() {
        return boardElementsSpaces;
    }

    public void clearLasers() {
        for (Space[] space : spaces) {
            for (Space value : space) {
                if (value == null) continue;
                value.clearLasersOnSpace();
            }
        }
    }

    /**
     * Sets the priority value for all players, and sorts them in so the player closet to the antenna goes first
     *
     * @author Michael Sylvest Bendtsen, s214954@dtu.dk
     */
    private Space antenna;
    public void updatePriorityList() {
        priorityList.clear();
        if (antenna == null) {
            antenna = findAntenna();
        }
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

    /**
     * Determine the priority of an individual player, by determining their distance from the antenna
     *
     * @author Michael Sylvest Bendtsen, s214954@dtu.dk
     * @param player to determine the priority we need the players position
     * @param antenna to determine the priority we need the antennas position
     * @return the players distance from the antenna, which is also their priority
     */
    public Integer determinePlayerPriority(Player player,Space antenna) {
        Space space = player.getSpace();
        if (space == null) 
            return -1;
        int x = antenna.x - space.x;
        int y = antenna.y - space.y;
        return Math.abs(x) + Math.abs(y);
    }

    /**
     * Looks through all board spaces and finds the antenna
     *
     * @author Michael Sylvest Bendtsen, s214954@dtu.dk
     * @return the location of the antenna
     */
    public Space findAntenna() {
        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                if (spaces[x][y] == null) continue;
                BoardElement boardElement = spaces[x][y].getBoardElement();
                if (boardElement instanceof BE_Antenna) {
                    return spaces[x][y];
                }
            }
        }
        System.out.println("Err: No Priority antenna found");
        return null;
    }

    public List<Space[][]> getSubBoards() {
        return this.subBoards;
    }

    public Space[][] getSubBoardOfSpace(Space space) throws RuntimeException {
        System.out.println("Looking for subboard");
        System.out.println("Playerspace: " + space.x + ", " + space.y);
        for (Space[][] subBoard : subBoards) {
            for (Space[] subBoardColumn : subBoard) {
                for (Space subBoardSpace : subBoardColumn) {
                    if (subBoardSpace != null) {
                        if (subBoardSpace == space) {
                            System.out.println("Found subboard with index: " + subBoards.indexOf(subBoard));
                            return subBoard;
                        }
                    }
                }
            }
        }
        throw new RuntimeException("Can't find space in sub boards.");
    }
    public Pair<Space, BE_Reboot> findRebootInSubBoard(Space[][] subBoard) {
        for (Space[] subBoardColumn : subBoard) {
            for (Space subBoardSpace : subBoardColumn) {
                if (subBoardSpace == null) continue;
                // Since there can be both reboot element and spawnpoint on the same subboard, we first check for reboot
                if (subBoardSpace.getBoardElement() instanceof BE_Reboot reboot) {
                    return new Pair<>(subBoardSpace, reboot);
                }
            }
        }

        // If no reboot element on subboard, return null and let the player reboot on their spawnpoint
        return null;
    }

    public int getNumberOfCheckpoints() {
        return numberOfCheckPoints;
    }
}
