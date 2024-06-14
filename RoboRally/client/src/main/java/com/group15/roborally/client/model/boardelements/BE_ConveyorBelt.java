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
package com.group15.model.boardelements;

import com.group15.roborally.controller.GameController;
import com.group15.model.*;

import com.group15.roborally.client.utils.ImageUtils;
import com.group15.roborally.server.model.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.group15.model.Heading.*;

/**
 * This class represents a conveyor belt on the board. When a player lands on a conveyor belt,
 * the player is moved in the direction of the conveyor belt. The strength of the conveyor belt
 * determines how many times the player is moved in the direction of the conveyor belt.
 * The conveyor belt can also rotate the player if the player lands on a conveyor belt that is
 * rotated 90 degrees compared to the conveyor belt the player was on.
 * 
 * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class BE_ConveyorBelt extends BoardElement {
    private final int strength;
    private final List<Space> connections = new ArrayList<>();

    /**
     * Constructor for the conveyor belt
     * @param direction the direction of the conveyor belt
     * @param strength the strength of the conveyor belt
     */
    public BE_ConveyorBelt(Heading direction, int strength) {
        super(null);
        if (strength < 1) { // Strength has to be at least 1.
            strength = 1;
        }
        this.strength = strength;
        setDirection(direction);
    }

    /**
     * Getter for the strength of the conveyor belt
     * @return the strength of the conveyor belt
     */
    public int getStrength() {
        return strength;
    }
    public List<Space> getConnections() {
        return connections;
    }
    public void clearConnections() {
        this.connections.clear();
    }
    public void addConnections(Space connection) {
        this.connections.add(connection);
    }

    public void updateConveyorBeltImage(int x, int y, Space[][] spaces) {
        setImage(ImageUtils.getRotatedImageByHeading(ImageUtils.getImageFromName("Board Pieces/" + ImageUtils.getUpdatedConveyorBeltImage(this, x, y, spaces)), this.direction));
    }

    /**
     * When a player lands on a conveyor belt, the player is moved in the direction of the conveyor belt.
     * The strength of the conveyor belt determines how many times the player is moved in the direction of the conveyor belt.
     * The player will be rotated to face the same direction as the conveyor belt they land on, if it was connected to the previous
     *     conveyor belt, and has a direction difference of 90 degrees.
     *
     * @param space the space where the player is located
     * @param gameController the game controller
     * @param actionQueue the queue of actions
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController, LinkedList<ActionWithDelay> actionQueue) {
        // Getting the player:
        com.group15.roborally.server.model.Player player = space.getPlayer();

        if (player == null) {
            return false;
        }

        // First we make a copy of the board to simulate it
        Space[][] boardSpaces = gameController.board.getSpaces();
        SimulatedSpace[][] simulatedSpaces = new SimulatedSpace[boardSpaces.length][boardSpaces[0].length];
        for (int x = 0; x < boardSpaces.length; x++) {
            for (int y = 0; y < boardSpaces[x].length; y++) {
                Space originalSpace = boardSpaces[x][y];
                if (originalSpace == null) continue;
                SimulatedSpace simulatedSpace = new SimulatedSpace(x, y);
                simulatedSpaces[x][y] = simulatedSpace;
                if (originalSpace.getBoardElement() instanceof BE_ConveyorBelt belt && belt.strength == this.strength) {
                    simulatedSpace.isSameType = true;
                    simulatedSpace.heading = belt.getDirection();
                }
                com.group15.roborally.server.model.Player playerOnSpace = originalSpace.getPlayer();
                if (playerOnSpace != null) {
                    simulatedSpace.player = playerOnSpace;
                }
                simulatedSpace.addWalls(originalSpace.getWalls());
            }
        }

        Space currentSpace = space;
        // We get the copy of this space.
        SimulatedSpace toSpace = simulatedSpaces[space.x][space.y];
        // For each time this type of conveyor belt can move a player:
        for (int i = 0; i < strength; i++) {
            Space nextSpace;
            // We check recursively if we can move this once in The Matrix starring Keanu Reeves.
            if (canMoveOnce(toSpace, simulatedSpaces, new ArrayList<>())) {
                // If we get here, it means we can move the player once.
                BE_ConveyorBelt currentConveyorBelt = ((BE_ConveyorBelt)currentSpace.getBoardElement());
                nextSpace = currentSpace.getSpaceNextTo(currentConveyorBelt.getDirection(), boardSpaces);
                if (nextSpace != null) {
                    // Rotate the robot.
                    if (nextSpace.getBoardElement() instanceof BE_ConveyorBelt nextBelt) {
                        if (nextBelt.direction != currentConveyorBelt.getDirection() && nextBelt.direction.opposite() != currentConveyorBelt.getDirection()) {
                            int clockwiseOrdinal = (currentConveyorBelt.getDirection().ordinal() + 1) % Heading.values().length;
                            if (nextBelt.getDirection().ordinal() == clockwiseOrdinal) {
                                player.setHeading(player.getHeading().next());
                            } else {
                                player.setHeading(player.getHeading().prev());
                            }
                        }
                    }
                } else {
                    player.setSpace(null); // The player fell off the board
                    return true;
                }
                player.setTemporarySpace(nextSpace);
                currentSpace = nextSpace;
            }
        }
        return true;
    }

    /**
     * Recursively checking if the player standing on this conveyor belt can be moved, by determining the future of the position
     *     of player's moving on the same type of conveyor belt.
     *
     * @param currentSpace The space the player starts on.
     * @param spaces A bi-dimensional array of the simulated spaces.
     * @param visitedSpaces Should be an empty array. Keeps track of the simulated spaces, in order to prevent an infinite loop/stack overflow.
     * @return Returns whether a player standing on this conveyor belt will be able to be moved.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private boolean canMoveOnce(SimulatedSpace currentSpace, SimulatedSpace[][] spaces, List<SimulatedSpace> visitedSpaces) {
        if (visitedSpaces.contains(currentSpace)) {
            // We have already checked here. This stops an infinite loop from happening.
            return false;
        }
        if (currentSpace.isSameType) {
            SimulatedSpace nextSpace = currentSpace.getSpaceNextTo(currentSpace.heading, spaces);
            if (nextSpace == null) {
                return true;
            }
            if (!currentSpace.getIsWallBetween(nextSpace)) {
                if (nextSpace.player == null) { // Next space is free!
                    // Move the simulation
                    nextSpace.player = currentSpace.player;
                    currentSpace.player = null;
                    return true;
                } else {
                    // Someone on the next space
                    if (nextSpace.isSameType) {
                        visitedSpaces.add(currentSpace);
                        // TODO: Check neighboring conveyor belts of "next conveyor belt", if someone is about to enter "next conveyor belt" at the same time. So many edge cases...
                        // If the person on the next space is on a conveyor belt of same type, recursively check if they can move.
                        boolean canMove = canMoveOnce(nextSpace, spaces, visitedSpaces);
                        // Move the simulation
                        if (canMove) {
                            nextSpace.player = currentSpace.player;
                            currentSpace.player = null;
                        }
                        return canMove;
                    } else {
                        // Next occupies space is not a ConveyorBelt.
                        return false;
                    }
                }
            } else {
                // There is a wall between.
                return false;
            }
        } else {
            // This is not a conveyor belt.
            return false;
        }
    }

    /**
     * A stripped down version of the Space class, used in conveyor belt logic to predict player movement on conveyor belts.
     *
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    static class SimulatedSpace {
        protected int x, y;
        protected boolean isSameType = false;
        protected Heading heading;
        protected Player player = null;
        private final List<Heading> walls = new ArrayList<>();
        protected SimulatedSpace(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public List<Heading> getWalls() {
            return walls;
        }
        public void addWalls(List<Heading> walls) {
            this.walls.addAll(walls);
        }

        protected Heading getDirectionToOtherSpace(SimulatedSpace otherSpace) {
            int dx = otherSpace.x - this.x;
            int dy = otherSpace.y - this.y;
            // Horizontal
            if (dx != 0) {
                switch (dx) {
                    case 1:
                        return EAST;
                    case -1:
                        return WEST;
                }
            }
            // Vertical
            if (dy != 0) {
                switch (dy) {
                    case 1:
                        return SOUTH;
                    case -1:
                        return NORTH;
                }
            }
            return NORTH;
        }
        protected boolean getIsWallBetween(SimulatedSpace otherSpace) {
            if (otherSpace == null) {
                System.out.println("ERROR in code. otherSpace is null. This method only takes two spaces next to each other (not diagonally). Check the SimulatedSpace.getDirectionToOtherSpace() method.");
                return false;
            }
            Heading directionToOtherSpace = getDirectionToOtherSpace(otherSpace);
            List<Heading> otherWallDirections = otherSpace.getWalls();
            return switch (directionToOtherSpace) {
                case EAST -> (walls.contains(EAST) || otherWallDirections.contains(WEST));
                case WEST -> (walls.contains(WEST) || otherWallDirections.contains(EAST));
                case SOUTH -> (walls.contains(SOUTH) || otherWallDirections.contains(NORTH));
                case NORTH -> (walls.contains(NORTH) || otherWallDirections.contains(SOUTH));
                default -> false;
            };
        }
        protected SimulatedSpace getSpaceNextTo(Heading direction, SimulatedSpace[][] spaces) {
            return switch (direction) {
                case SOUTH -> {
                    if (this.y + 1 >= spaces[x].length) {
                        yield null;
                    }
                    yield spaces[this.x][this.y + 1]; // out of bounds
                }
                case WEST -> {
                    if (this.x - 1 < 0) {
                        yield null;
                    }
                    yield spaces[this.x - 1][this.y]; // out of bounds
                }
                case NORTH -> {
                    if (this.y - 1 < 0) {
                        yield null;
                    }
                    yield spaces[this.x][this.y - 1]; // out of bounds
                }
                case EAST -> {
                    if (this.x + 1 >= spaces.length) {
                        yield null;
                    }
                    yield spaces[this.x + 1][this.y]; // out of bounds
                }
                default -> {
                    System.out.println("ERROR in Space.getSpaceNextTo()");
                    yield null;
                }
            };
        }
    }
}
