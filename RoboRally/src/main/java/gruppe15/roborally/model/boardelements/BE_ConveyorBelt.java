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

import gruppe15.roborally.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class BE_ConveyorBelt extends BoardElement {

    private Heading heading;
    private int strength;

    public BE_ConveyorBelt(Heading heading, int strength) {
        this.heading = heading;
        if (strength < 1) { // Strength has to be at least 1.
            strength = 1;
        }
        this.strength = strength;
    }

    @Override
    public void calculateImage(int x, int y, Space[][] spaces) {
        StringBuilder imageNameBuilder = new StringBuilder();

        // Green or blue
        imageNameBuilder.append(strength == 1 ? "green" : "blue");

        // Neighbors
        int noOfNeighbors = 0;
        boolean[] relativeNeighbors = new boolean[4];
        Space space = spaces[x][y];

        for (int i = 0; i < 4; i++) {
            Heading relativeDirection = Heading.values()[(heading.ordinal() + i) % 4];
            Space neighborSpace = space.getSpaceNextTo(relativeDirection, spaces);
            if (i == 0 || (neighborSpace != null && neighborSpace.getBoardElement() instanceof BE_ConveyorBelt)) {
                if (i == 0 || ((BE_ConveyorBelt) neighborSpace.getBoardElement()).getHeading().opposite() == relativeDirection) {
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
        setImage(imageNameBuilder.toString(), heading);
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull Board board, LinkedList<ActionWithDelay> actionQueue) {
        Player player = space.getPlayer();
        if (player != null) {
            Space toSpace = space.getSpaceNextTo(heading, board.getSpaces());
            if (toSpace == null)
                return false;
            if (toSpace.getPlayer() == null) {
                space.setPlayer(null);
                toSpace.setPlayer(player);
                return true;
            }
        }
        return false;
    }

}
