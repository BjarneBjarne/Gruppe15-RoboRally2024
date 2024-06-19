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
package com.group15.roborally.client.model;

import com.group15.observer.Subject;
import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.boardelements.*;
import com.group15.roborally.client.model.damage.DamageType;
import com.group15.roborally.client.model.events.PhaseChangeListener;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.group15.roborally.client.BoardOptions.NO_OF_PLAYERS;
import static com.group15.roborally.client.model.Phase.INITIALIZATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {
    private Integer gameId;
    private final String courseName;

    public final int width;
    public final int height;
    private final Space[][] spaces;
    private List<Space[][]> subBoards;

    private final List<Player> players = new ArrayList<>();
    private final Queue<Player> priorityList = new ArrayDeque<>();
    private Player currentPlayer;

    private Phase currentPhase = INITIALIZATION;
    private int currentRegister = 0;

    final public int NO_OF_CHECKPOINTS;

    private final LinkedList<ActionWithDelay> boardActionQueue = new LinkedList<>();

    private UpgradeShop upgradeShop;

    private int moveCounter = 0; // The counter for how many moves have been made
    private boolean stepMode = false;

    public Board(List<Space[][]> subBoards, Space[][] spaces, String courseName, int noOfCheckpoints) {
        this.subBoards = subBoards;
        this.spaces = spaces;
        this.courseName = courseName;
        NO_OF_CHECKPOINTS = noOfCheckpoints;
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

    public String getCourseName() {
        return courseName;
    }

    public void initializeUpgradeShop() {
        this.upgradeShop = new UpgradeShop(this);
    }

    public UpgradeShop getUpgradeShop() {
        return upgradeShop;
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

    public void setPriorityList(List<Player> newPriorityList) {
        priorityList.clear();
        priorityList.addAll(newPriorityList);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        if (player != this.currentPlayer && players.contains(player)) {
            this.currentPlayer = player;
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
    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(Phase currentPhase) {
        if (currentPhase != this.currentPhase) {
            this.currentPhase = currentPhase;
            phaseChangeListeners.forEach(listener -> listener.onPhaseChange(currentPhase));
            notifyChange();
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

    /**
     * Method for getting the current game status.
     * @return A string of text containing information about the board as well as the current player.
     */
    public String getStatusMessage() {
        AtomicInteger noOfDamageCards = new AtomicInteger();

        for (CommandCard commandCard : getCurrentPlayer().getProgrammingDeck()) {
            if (commandCard == null) {
                continue;
            }
            for (DamageType damageType : DamageType.values()) {
                if (damageType.getCommandCardType() == commandCard.command) {
                    noOfDamageCards.getAndIncrement();
                }
            }
        }

        return "Phase: " + getCurrentPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Player damage cards = " + noOfDamageCards +
                ", Register: " + getCurrentRegister() + ", MoveCount: " + getMoveCounter();// +
                //",  ";
    }

    /**
     * Creates an array that holds "a list of spaces" for each different board element.
     * This way, we only calculate once, where the different board elements are on the board,
     *     for when we need to execute board elements actions.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private List<Space>[] boardElementsSpaces;
    private void updateBoardElementSpaces() {
        boardElementsSpaces = new ArrayList[7];
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
                } else if (space.getCheckPoint() != null) {
                    boardElementsSpaces[6].add(space);
                }
            }
        }
    }

    /**
     * Adds the board element actions of all board elements of a board element type to the action queue.
     * @param gameController
     * @param boardElementsIndex The index in boardElementsSpaces that corresponds to a board element type.
     * @param debugBoardElementName The action message.
     */
    public void queueBoardElementsWithIndex(GameController gameController, int boardElementsIndex, String debugBoardElementName) {
        if (boardElementsIndex < 0 || boardElementsIndex >= boardElementsSpaces.length) return;

        boardActionQueue.addLast(new ActionWithDelay(() -> {
            for (Space space : boardElementsSpaces[boardElementsIndex]) {
                if (space.getBoardElement() == null) {
                    space.getCheckPoint().doAction(space, gameController, boardActionQueue);
                    continue;
                }
                space.getBoardElement().doAction(space, gameController, boardActionQueue);
            }
            for (int i = 0; i < getNoOfPlayers(); i++) {
                Player player = getPlayer(i);
                player.goToTemporarySpace();
            }
        }, 100, debugBoardElementName));
    }

    public void queueClearLasers() {
        boardActionQueue.addLast(new ActionWithDelay(() -> {
            for (Space[] space : spaces) {
                for (Space value : space) {
                    if (value == null) continue;
                    value.clearLasersOnSpace();
                }
            }
        }, 0));
    }

    /**
     * Sets the priority value for all players and puts them in the priority queue.
     * Sorted by the players distance to the antenna, with the closet going first.
     * On distance tiebreakers, priority is decided by the antennas angle to the player, going clockwise.
     * @author Michael Sylvest Bendtsen, s214954@dtu.dk
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private Space antennaSpace;
    public void updatePriorityList() {
        priorityList.clear();
        if (antennaSpace == null) {
            antennaSpace = findAntenna();
        }

        BE_Antenna antenna = (BE_Antenna) antennaSpace.getBoardElement();

        // Getting player distances
        Map<Integer, List<Player>> distanceMap = new HashMap<>();
        for (Player player : players) {
            int playerDistance = getPlayerDistance(player, antennaSpace);
            distanceMap.computeIfAbsent(playerDistance, k -> new ArrayList<>()).add(player);
        }

        // Determining distance tie-breakers
        Map<Integer, Player> priorityMap = new HashMap<>();
        for (int distance : distanceMap.keySet()) {
            List<Player> playersWithSameDistance = distanceMap.get(distance);
            // If there is only one player at this distance, put them in the priorityMap and continue.
            if (playersWithSameDistance.size() == 1) {
                priorityMap.put(distance * NO_OF_PLAYERS, playersWithSameDistance.getFirst());
                continue;
            }
            // Getting the angles from the antenna to players
            Map<Double, Player> angleMap = new HashMap<>();
            for (Player player : playersWithSameDistance) {
                double angleToPlayerRadians = getAngleToPlayerRadians(player, antenna);
                angleMap.put(angleToPlayerRadians, player);
            }
            // Putting the players with same distance in the priority list based on the antennas angle to them.
            List<Map.Entry<Double, Player>> sortedByAngle = angleMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .toList();

            for (int i = 0; i < sortedByAngle.size(); i++) {
                priorityMap.put((distance * NO_OF_PLAYERS) + i, sortedByAngle.get(i).getValue());
            }
        }

        // Adding players to the priorityList
        //System.out.println("New priority:");
        List<Map.Entry<Integer, Player>> newPriorityList = priorityMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();
        for (int i = 0; i < newPriorityList.size(); i++) {
            Player player = newPriorityList.get(i).getValue();
            player.setPriority(i);
            priorityList.add(player);
            //System.out.println("Player \"" + player.getName() + "\": " + i + ". Calculated priority: " + newPriorityList.get(i).getKey());
        }
    }

    /**
     * Method for getting the angle from the antenna direction to the player in a clockwise rotation.
     * @param player
     * @param antenna
     * @return Returns the angle in radians between (0 and 2 * Pi).
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private double getAngleToPlayerRadians(Player player, BE_Antenna antenna) {
        double deltaX = player.getSpace().x - antennaSpace.x;
        double deltaY = player.getSpace().y - antennaSpace.y;

        double angleToPlayerRadians = Math.atan2(deltaY, deltaX);
        angleToPlayerRadians += Math.PI / 2; // Relative to Heading.SOUTH which is positive Y and is the first ordinal in the Heading enum.
        angleToPlayerRadians += (Math.PI / 2) * antenna.getDirection().ordinal(); // Relative to antenna's direction
        if (angleToPlayerRadians < 0) {
            angleToPlayerRadians += 2 * Math.PI;
        } else if (angleToPlayerRadians >= 2 * Math.PI) {
            angleToPlayerRadians -= 2 * Math.PI;
        }

        return angleToPlayerRadians;
    }

    /**
     * Gets the players distance from the antenna
     *
     * @author Michael Sylvest Bendtsen, s214954@dtu.dk
     * @param player to determine the priority we need the players position
     * @param antenna to determine the priority we need the antennas position
     * @return the players distance from the antenna, which is also their priority
     */
    public Integer getPlayerDistance(Player player, Space antenna) {
        Space space = player.getSpace();
        if (space == null) 
            return -1;
        int xDist = antenna.x - space.x;
        int yDist = antenna.y - space.y;
        return Math.abs(xDist) + Math.abs(yDist);
    }

    /**
     * Looks through all board spaces and finds the antenna
     *
     * @author Michael Sylvest Bendtsen, s214954@dtu.dk
     * @return the location of the antenna
     */
    public Space findAntenna() {
        for (Space[] spaceColumns : spaces) {
            for (Space space : spaceColumns) {
                if (space == null) continue;
                BoardElement boardElement = space.getBoardElement();
                if (boardElement instanceof BE_Antenna) {
                    return space;
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
        for (Space[][] subBoard : subBoards) {
            for (Space[] subBoardColumn : subBoard) {
                for (Space subBoardSpace : subBoardColumn) {
                    if (subBoardSpace != null) {
                        if (subBoardSpace == space) {
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
        return NO_OF_CHECKPOINTS;
    }

    /**
     * Method for queuing all players lasers.
     * This is the default way to make a player shoots their laser.
     */
    public void queuePlayerLasers() {
        updatePriorityList();
        while (!priorityList.isEmpty()) {
            Player player = priorityList.poll();

            // First we make an action that lasts 150ms.
            boardActionQueue.addLast(new ActionWithDelay(() -> {
                // When the player is about to shoot, we immediately queue to clear the lasers as the next action, ensuring the lasers are cleared in between player lasers.
                boardActionQueue.addFirst(new ActionWithDelay(this::queueClearLasers, 0, ""));
                // Tell the EventHandler that we want to shoot.
                EventHandler.event_PlayerShootStart(player);
            }, 150, "Player: \"" + player.getName() + "\" laser"));
        }
    }

    public LinkedList<ActionWithDelay> getBoardActionQueue() {
        return boardActionQueue;
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
    public void movePlayerToSpace(Player player, Space nextSpace, GameController gameController) {
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
                        EventHandler.event_PlayerPush(spaces, player, playersToPush, pushDirection); // WARNING: Can lead to infinite loop
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
        EventHandler.event_PlayerMove(player, nextSpace, gameController);
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
        Space nextSpace = space.getSpaceNextTo(direction, spaces);
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
}
