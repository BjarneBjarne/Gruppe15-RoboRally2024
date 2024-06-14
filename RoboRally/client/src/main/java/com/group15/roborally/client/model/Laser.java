package com.group15.roborally.client.model;

import com.group15.roborally.client.model.boardelements.BE_Antenna;
import com.group15.roborally.client.model.boardelements.BE_PushPanel;
import com.group15.roborally.client.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is meant for single use.
 * A new laser should be created each time a laser should be fired, to calculate the spaces hit depending on card modifier, etc.
 */
public class Laser {
    public final Space origin;
    public final Heading direction;
    public final List<Space> spacesHit = new ArrayList<>();
    public final com.group15.roborally.client.model.Player owner;
    public final List<Class<?>> objectTypesToCollideWith = new ArrayList<>();

    private boolean iterationComplete = false;

    public class LaserOnSpace {
        private final String imageName;
        public LaserOnSpace(String imageName) {
            this.imageName = imageName;
        }
        public String getImageName() {
            return "Lasers/" + imageName + ".png";
        }
        public Heading getDirection() {
            return direction;
        }
    }

    /**
     * Constructor for a new laser. This is used for both player lasers and board lasers.
     * @param origin The space where the laser should start.
     * @param direction The direction the laser is going .
     * @param owner Indicates that the laser came from a player. The owner is the player shooting the laser.
     * @param objectTypesToCollideWith The class types to collide with. Check the Laser class to see which classes are being looked for. Currently only looks for Player.class and Space.class.
     */
    public Laser(Space origin, Heading direction, com.group15.roborally.client.model.Player owner, Class<?>... objectTypesToCollideWith) {
        this.origin = origin;
        this.direction = direction;
        this.owner = owner;
        this.objectTypesToCollideWith.addAll(List.of(objectTypesToCollideWith)); // Arrays.asList(objectTypesToCollideWith)
    }

    public Runnable startLaser(Space[][] boardSpaces) {
        return () -> {
            performSpaceIteration(boardSpaces, direction);
            setIterationComplete(true);
        };
    }

    private void performSpaceIteration(Space[][] boardSpaces, Heading direction) {
        int dx = direction == Heading.WEST ? -1 : direction == Heading.EAST ? 1 : 0;
        int dy = direction == Heading.NORTH ? -1 : direction == Heading.SOUTH ? 1 : 0;
        int x = origin.x;
        int y = origin.y;
        Space nextSpace = origin.getSpaceNextTo(direction, boardSpaces);

        boolean thisHasWall = origin.getWalls().contains(direction);
        boolean otherHasWall = nextSpace != null && nextSpace.getWalls().contains(direction.opposite());

        if (thisHasWall || otherHasWall) { // If the source is looking into a wall, stop here
            if (owner != null) { // If there is an owner, a player shot the laser.
                addLaserPiece(origin, "Laser_StartPlayerBlocked");
            } else { // Else, board laser
                addLaserPiece(origin, "Laser_StartBoardBlocked");
            }
            if (otherHasWall) {
                addLaserPiece(nextSpace, "Laser_WallHitLower");
            }
            return;
        }

        boolean hitSomething = false;
        while (!hitSomething && (x >= 0 && x < boardSpaces.length && y >= 0 && y < boardSpaces[0].length)) {
            Space space = boardSpaces[x][y];
            if (space != null) {
                spacesHit.add(space);
                com.group15.roborally.client.model.Player playerOnSpace = space.getPlayer();
                nextSpace = space.getSpaceNextTo(direction, boardSpaces);
                String laserName = "Laser_";

                if (playerOnSpace != null && playerOnSpace != owner && objectTypesToCollideWith.contains(Player.class)) { // Player hit
                    hitSomething = true;
                    laserName += "PlayerHit";
                } else if (x == origin.x && y == origin.y) {
                    laserName += "Start";
                    if (owner != null) { // If there is an owner, a player shot the laser.
                        laserName += "Player";
                    } else { // Else, board laser
                        laserName += "Board";
                    }
                } else if ((space.getWalls().contains(direction) ||
                        (space.getBoardElement() instanceof BE_PushPanel panel && panel.getDirection() == direction))
                        && objectTypesToCollideWith.contains(Space.class)) { // Object in the way at this space.
                    hitSomething = true;
                    laserName += "WallHitUpper";
                } else if (nextSpace != null
                        && (nextSpace.getWalls().contains(direction.opposite()) ||
                        (nextSpace.getBoardElement() instanceof BE_PushPanel panel && panel.getDirection() == direction.opposite()) ||
                        (nextSpace.getBoardElement() instanceof BE_Antenna))
                        && objectTypesToCollideWith.contains(Space.class)) { // Object in the way at next space.
                    hitSomething = true;
                    laserName += "Full";
                    addLaserPiece(nextSpace, "Laser_WallHitLower"); // Adding the next laser image piece.
                } else {
                    laserName += "Full";
                }

                addLaserPiece(space, laserName);
            }

            // Move to the next space
            x += dx;
            y += dy;
        }
    }

    private void addLaserPiece(Space space, String imageName) {
        space.addLaserOnSpace(new LaserOnSpace(imageName));
    }

    private synchronized void setIterationComplete(boolean value) {
        iterationComplete = value;
        notifyAll();
    }

    public List<Space> getSpacesHit() throws InterruptedException {
        // Wait until iteration is complete
        synchronized (this) {
            while (!iterationComplete) {
                wait();
            }
            // Return the list of spaces hit
            return spacesHit;
        }
    }
}
