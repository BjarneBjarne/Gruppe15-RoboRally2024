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
    private BoardElement boardElement;

    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
    }

    public Player getPlayer() {
        return player;
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

    public void setBoardElement(BoardElement boardElement) {
        this.boardElement = boardElement;
    }

    /**
     * Should only be used on two spaces next to each other (not diagonally).
     * @return Returns whether there's a wall separating the two spaces.
     */
    public boolean wallBetween(Space otherSpace) {
        int dx = otherSpace.x - this.x;
        int dy = otherSpace.y - this.y;

        int x = origin.x;
        int y = origin.y;
        while (x >= 0 && x < boardSpaces.length && y >= 0 && y < boardSpaces[0].length) {
            Space space = boardSpaces[x][y];
            // If there is an object on the space, break the loop.
            if (space.getBoardElement() != null) {
                break;
            }
            spacesHit.add(space);
            // If there is a player, we still add the space, but break out of loop.
            if (space.getPlayer() != null) {
                break;
            }
            x += dx;
            y += dy;
        }
    }
}
