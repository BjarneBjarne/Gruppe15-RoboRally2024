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
import gruppe15.roborally.model.boardelements.BE_Antenna;
import gruppe15.roborally.model.boardelements.BE_PushPanel;
import gruppe15.roborally.model.boardelements.BoardElement;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

import static gruppe15.roborally.model.Heading.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Space extends Subject {

    transient public final Board board;

    public final int x;
    public final int y;

    transient private Player player;
    transient private BoardElement boardElement;
    transient private Image backgroundImage;
    transient private final List<Heading> walls = new ArrayList<>();
    transient private final List<Laser.LaserOnSpace> lasersOnSpace = new ArrayList<>();

    public Space(Board board, int x, int y, BoardElement boardElement, List<Heading> walls) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
        this.boardElement = boardElement;
        if (walls != null) {
            this.walls.addAll(walls);
        }
    }

    public Space(Board board, int x, int y, BoardElement boardElement) {
        this(board, x, y, boardElement, null);
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setBoardElement(BoardElement boardElement) {
        this.boardElement = boardElement;
    }

    public Player getPlayer() {
        if (player != null) {
            return player;
        } else {
            return null;
        }
    }

    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

    public void clicked() {
        notifyChange();
    }

    public BoardElement getBoardElement() {
        return boardElement;
    }

    public boolean hasWall() {
        return !walls.isEmpty();
    }
    public List<Heading> getWalls() {
        return walls;
    }
    public void addWall(Heading wall) {
        this.walls.add(wall);
    }
    public void addWalls(List<Heading> walls) {
        this.walls.addAll(walls);
    }

    // For SpaceView, so it can update the laser image on this space.
    public void addLaserOnSpace(Laser.LaserOnSpace laser) {
        this.lasersOnSpace.add(laser);
        notifyChange();
    }
    public void clearLasersOnSpace() {
        this.lasersOnSpace.clear();
        notifyChange();
    }
    public List<Laser.LaserOnSpace> getLasersOnSpace() {
        return this.lasersOnSpace;
    }

    public int getDistanceFromOtherSpace(Space otherSpace) {
        int distance = 0;
        double xDist, yDist;

        xDist = otherSpace.x - this.x;
        yDist = otherSpace.y - this.y;

        distance = (int)Math.sqrt(Math.pow(xDist, 2.0) + Math.pow(yDist, 2.0));

        return distance;
    }

    /**
     * Should only be used on two spaces next to each other (not diagonally).
     * @return Returns whether there's a wall separating the two spaces.
     */
    public boolean getIsWallBetween(Space otherSpace) {
        if (otherSpace == null) {
            System.out.println("ERROR in code. otherSpace is null. This method only takes two spaces next to each other (not diagonally). Check the Space.getDirectionToOtherSpace() method.");
            return false;
        }

        Heading directionToOtherSpace = getDirectionToOtherSpace(otherSpace);

        // Check for walls
        if (walls.contains(directionToOtherSpace) || otherSpace.getWalls().contains(directionToOtherSpace.opposite())) {
            return true;
        }

        // Check for antenna
        if (otherSpace.getBoardElement() instanceof BE_Antenna) {
            return true;
        }

        // Check for push panels
        return (boardElement instanceof BE_PushPanel myPushPanel && myPushPanel.getDirection().opposite() == directionToOtherSpace) ||
                (otherSpace.getBoardElement() instanceof BE_PushPanel otherPushPanel && otherPushPanel.getDirection() == directionToOtherSpace);
    }

    public Heading getDirectionToOtherSpace(Space otherSpace) {
        int dx = otherSpace.x - this.x;
        int dy = otherSpace.y - this.y;

        // TODO: Make exceptions throws instead of if-statements.
        if (dx == 0 && dy == 0) {
            System.out.println("Getting space {" + this.x + ", " + this.y + "} and " + "{" + otherSpace.x + ", " + otherSpace.y + "}");
            System.out.println("ERROR in code. Got the same space twice. This method only takes two spaces next to each other (not diagonally). Check the Space.getDirectionToOtherSpace() method.");
            return NORTH;
        }
        if (Math.abs(dx) > 0 && Math.abs(dy) > 0) {
            System.out.println("Getting space {" + this.x + ", " + this.y + "} and " + "{" + otherSpace.x + ", " + otherSpace.y + "}");
            System.out.println("ERROR in code. Spaces too far apart. This method only takes two spaces next to each other (not diagonally). Check the Space.getDirectionToOtherSpace() method.");
            return NORTH;
        }
        if (dx != 0 && dy != 0) {
            System.out.println("Getting space {" + this.x + ", " + this.y + "} and " + "{" + otherSpace.x + ", " + otherSpace.y + "}");
            System.out.println("ERROR in code. Can't take diagonal spaces. This method only takes two spaces next to each other (not diagonally). Check the Space.getDirectionToOtherSpace() method.");
            return NORTH;
        }

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

        // TODO: Make exception throw instead of if-statement.
        // We shouldn't get to here
        System.out.println("Getting space {" + this.x + ", " + this.y + "} and " + "{" + otherSpace.x + ", " + otherSpace.y + "}");
        System.out.println("ERROR in code. Something went wrong. Check the Space.wallBetween() method.");
        return NORTH;
    }

    public Space getSpaceNextTo(Heading direction, Space[][] spaces) {
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
        };
    }
    public Image getImage() {
        return backgroundImage;
    }

    public void updateSpace() {
        notifyChange();
    }
}
