package gruppe15.roborally.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is meant for single use.
 * A new laser should be created each time a laser should be fired, to calculate the spaces hit depending on card modifier, etc.
 */
public class Laser {
    private final Space origin;
    private final Heading direction;
    private final List<Space> spacesHit = new ArrayList<>();
    private boolean iterationComplete = false;
    private final Player owner;

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

    public Laser(Space origin, Heading direction, Player owner) {
        this.origin = origin;
        this.direction = direction;
        this.owner = owner;
    }
    public Laser(Space origin, Heading direction) {
        this(origin, direction, null);
    }

    public Runnable startLaser(Space[][] boardSpaces) {
        return () -> {
            performSpaceIteration(boardSpaces, direction, spacesHit);
            setIterationComplete(true);
        };
    }

    private void performSpaceIteration(Space[][] boardSpaces, Heading direction, List<Space> spacesHit) {
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
                Player playerOnSpace = space.getPlayer();
                nextSpace = space.getSpaceNextTo(direction, boardSpaces);
                String laserName = "Laser_";

                if (x == origin.x && y == origin.y) {
                    laserName += "Start";
                    if (owner != null) { // If there is an owner, a player shot the laser.
                        laserName += "Player";
                    } else { // Else, board laser
                        laserName += "Board";
                    }
                } else if (playerOnSpace != null) { // Player hit
                    if (playerOnSpace != owner) {
                        hitSomething = true;
                        laserName += "PlayerHit";
                    }
                } else if (space.getWalls().contains(direction)) { // Wall
                    hitSomething = true;
                    laserName += "WallHitUpper";
                } else if (nextSpace != null && nextSpace.getWalls().contains(direction.opposite())) {
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
