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
import javafx.scene.image.Image;

import java.util.Objects;

import static gruppe15.roborally.model.Heading.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Space extends Subject {

    public final Board board;

    public final int x;
    public final int y;

    private Player player;
    private final BoardElement boardElement;
    private final Image image;

    public Space(Board board, int x, int y) {
        this(board, x, y, null);
    }

    public Space(Board board, int x, int y, BoardElement boardElement) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
        //this.boardElement = new BoardElement(false, false, NORTH);
        this.boardElement = boardElement;
        image = getInitializedSpaceImage();
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

    public BoardElement getBoardElement() {
        return boardElement;
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

        boolean thisHasWall;
        Heading thisWallDirection = NORTH;
        if (this.getBoardElement() != null) {
            thisHasWall = this.getBoardElement().getHasWall();
            thisWallDirection = this.getBoardElement().getWallDirection();
        } else {
            thisHasWall = false;
        }

        boolean otherHasWall;
        Heading otherWallDirection = NORTH;
        if (otherSpace.getBoardElement() != null) {
            otherHasWall = otherSpace.getBoardElement().getHasWall();
            otherWallDirection = otherSpace.getBoardElement().getWallDirection();
        } else {
            otherHasWall = false;
        }

        switch (directionToOtherSpace) {
            case EAST:
                return (thisHasWall && thisWallDirection == EAST) || (otherHasWall && otherWallDirection == WEST);
            case WEST:
                return (thisHasWall && thisWallDirection == WEST) || (otherHasWall && otherWallDirection == EAST);
            case SOUTH:
                return (thisHasWall && thisWallDirection == SOUTH) || (otherHasWall && otherWallDirection == NORTH);
            case NORTH:
                return (thisHasWall && thisWallDirection == NORTH) || (otherHasWall && otherWallDirection == SOUTH);
            default:
                return false;
        }

        //System.out.println("Getting space {" + this.x + ", " + this.y + "} and " + "{" + otherSpace.x + ", " + otherSpace.y + "}");
        //System.out.println("ERROR in code. Something went wrong. Check the Space.wallBetween() method.");
        //return false;
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

    private Image getInitializedSpaceImage() {
        String imagePath;
        if (boardElement == null) {
            imagePath = "/gruppe15/roborally/images/empty.png";
            try {
                return new Image(Objects.requireNonNull(Space.class.getResourceAsStream(imagePath)));
            } catch (Exception e) {
                System.out.println("Error importing image with path: " + imagePath);
                return null;
            }
        } else {
            return boardElement.getImage();
        }
    }
    public Image getImage() {
        return image;
    }
}
