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
package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.*;
import javafx.scene.image.Image;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static gruppe15.roborally.model.Heading.*;

/**
 * This class represents a conveyor belt on the board. When a player lands on a conveyor belt,
 * the player is moved in the direction of the conveyor belt. The strength of the conveyor belt
 * determines how many times the player is moved in the direction of the conveyor belt.
 * The conveyor belt can also rotate the player if the player lands on a conveyor belt that is
 * rotated 90 degrees compared to the conveyor belt the player was on.
 * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
 */
public class BE_ConveyorBelt extends BoardElement {
    private final int strength;

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
        setElemDirection(direction);
    }

    /**
     * Getter for the strength of the conveyor belt
     * @return the strength of the conveyor belt
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Calculates the image of the conveyor belt based on the direction and the number of neighbors
     * @param x the x-coordinate of the conveyor belt
     * @param y the y-coordinate of the conveyor belt
     * @param spaces the spaces on the board
     */
    public void calculateImage(int x, int y, Space[][] spaces) {
        StringBuilder imageNameBuilder = new StringBuilder();

        // Green or blue
        imageNameBuilder.append(strength == 1 ? "green" : "blue");

        // Neighbors
        int noOfNeighbors = 0;
        boolean[] relativeNeighbors = new boolean[4];
        Space space = spaces[x][y];

        for (int i = 0; i < 4; i++) {
            Heading relativeDirection = Heading.values()[(elemDirection.ordinal() + i) % 4];
            Space neighborSpace = space.getSpaceNextTo(relativeDirection, spaces);
            if (i == 0 || (neighborSpace != null && neighborSpace.getBoardElement() instanceof BE_ConveyorBelt)) {
                if (i == 0 || ((BE_ConveyorBelt) neighborSpace.getBoardElement()).getElemDirection().opposite() == relativeDirection) {
                    relativeNeighbors[i] = true;
                    noOfNeighbors++;
                }
            }
        }

        // Building string
        switch (noOfNeighbors) {
            case 1:
                imageNameBuilder.append("Straight");
                break;
            case 2:
                // Adjust the conditions for alignment based on the relative neighbors' indexes
                if (relativeNeighbors[2]) {
                    imageNameBuilder.append("Straight");
                } else {
                    imageNameBuilder.append("turn").append(relativeNeighbors[1] ? "Right" : "Left");
                }
                break;
            case 3:
                // Adjust the conditions for alignment based on the relative neighbors' indexes
                imageNameBuilder.append("T").append(relativeNeighbors[2] ? (relativeNeighbors[1] ? "Right" : "Left") : "Sides");
                break;
            default:
                break;
        }
        imageNameBuilder.append(".png");
        //System.out.println("Image: " + imageNameBuilder + " at '" + x + ", " + y + "' pointing " + heading + ", has " + noOfNeighbors + " neighbors!");
        setElemDirection(elemDirection);
        setImage(imageNameBuilder.toString());
    }

    /**
     * When a player lands on a conveyor belt, the player is moved in the direction of the conveyor belt.
     * The strength of the conveyor belt determines how many times the player is moved in the direction of the conveyor belt.
     * The conveyor belt can also rotate the player if the player lands on a conveyor belt that is rotated 90 degrees
     * compared to the conveyor belt the player was on.
     * @param space the space where the player is located
     * @param gameController the game controller
     * @param actionQueue the queue of actions
     */
    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController, LinkedList<ActionWithDelay> actionQueue) {
        // First we make a copy of the board to simulate it
        Space[][] boardSpaces = gameController.board.getSpaces();
        SimulatedSpace[][] simulatedSpaces = new SimulatedSpace[boardSpaces.length][boardSpaces[0].length];
        for (int x = 0; x < boardSpaces.length; x++) {
            for (int y = 0; y < boardSpaces[x].length; y++) {
                Space originalSpace = boardSpaces[x][y];
                SimulatedSpace simulatedSpace = new SimulatedSpace(x, y);
                simulatedSpaces[x][y] = simulatedSpace;
                if (originalSpace.getBoardElement() instanceof BE_ConveyorBelt belt && belt.strength == this.strength) {
                    simulatedSpace.isSameType = true;
                    simulatedSpace.heading = belt.getElemDirection();
                }
                Player playerOnSpace = originalSpace.getPlayer();
                if (playerOnSpace != null) {
                    simulatedSpace.player = playerOnSpace;
                }
                simulatedSpace.addWalls(originalSpace.getWalls());
            }
        }

        // Getting the player:
        Player player = space.getPlayer();
        Space currentSpace = space;
        // We get the copy of this space.
        SimulatedSpace toSpace = simulatedSpaces[space.x][space.y];
        // For each time this type of conveyor belt can move a player:
        for (int i = 0; i < strength; i++) {
            // We check recursively if we can move this once in The Matrix starring Keanu Reeves.
            if (canMoveOnce(toSpace, simulatedSpaces, new ArrayList<>())) {
                // If we get here, it means we can move the player once.
                BE_ConveyorBelt currentConveyorBelt = ((BE_ConveyorBelt)currentSpace.getBoardElement());
                Space nextSpace = currentSpace.getSpaceNextTo(currentConveyorBelt.getElemDirection(), boardSpaces);
                // Rotate the robot.
                if (nextSpace.getBoardElement() instanceof BE_ConveyorBelt nextBelt) {
                    if (nextBelt.elemDirection != currentConveyorBelt.getElemDirection()) {
                        int clockwiseOrdinal = (currentConveyorBelt.getElemDirection().ordinal() + 1) % Heading.values().length;
                        if (nextBelt.getElemDirection().ordinal() == clockwiseOrdinal) {
                            player.setHeading(player.getHeading().next());
                        } else {
                            player.setHeading(player.getHeading().prev());
                        }
                    }
                }
                player.setTemporarySpace(nextSpace);
                currentSpace = nextSpace;
            }
        }
        return false;
    }

    private boolean canMoveOnce(SimulatedSpace currentSpace, SimulatedSpace[][] spaces, List<SimulatedSpace> visitedSpaces) {
        if (visitedSpaces.contains(currentSpace)) {
            // We have already checked here. This stops an infinite loop from happening.
            return false;
        }
        if (currentSpace.isSameType) {
            SimulatedSpace nextSpace = currentSpace.getSpaceNextTo(currentSpace.heading, spaces);
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
                System.out.println("ERROR in code. otherSpace is null. This method only takes two spaces next to each other (not diagonally). Check the Space.getDirectionToOtherSpace() method.");
                return false;
            }
            Heading directionToOtherSpace = getDirectionToOtherSpace(otherSpace);
            List<Heading> otherWallDirections = otherSpace.getWalls();
            switch (directionToOtherSpace) {
                case EAST:
                    return (walls.contains(EAST) || otherWallDirections.contains(WEST));
                case WEST:
                    return (walls.contains(WEST) || otherWallDirections.contains(EAST));
                case SOUTH:
                    return (walls.contains(SOUTH) || otherWallDirections.contains(NORTH));
                case NORTH:
                    return (walls.contains(NORTH) || otherWallDirections.contains(SOUTH));
                default:
                    return false;
            }
        }
        protected SimulatedSpace getSpaceNextTo(Heading direction, SimulatedSpace[][] spaces) {
            switch (direction) {
                case SOUTH:
                    if (this.y + 1 >= spaces[x].length) {
                        return null; // out of bounds
                    }
                    return spaces[this.x][this.y + 1];
                case WEST:
                    if (this.x - 1 < 0) {
                        return null; // out of bounds
                    }
                    return spaces[this.x - 1][this.y];
                case NORTH:
                    if (this.y - 1 < 0) {
                        return null; // out of bounds
                    }
                    return spaces[this.x][this.y - 1];
                case EAST:
                    if (this.x + 1 >= spaces.length) {
                        return null; // out of bounds
                    }
                    return spaces[this.x + 1][this.y];
                default:
                    System.out.println("ERROR in Space.getSpaceNextTo()");
                    return null;
            }
        }
    }
}
