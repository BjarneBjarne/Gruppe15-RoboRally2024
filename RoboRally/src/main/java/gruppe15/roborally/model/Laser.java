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

        // Start one space in front of the owner
        /*if (owner != null) {
            x += dx;
            y += dy;
        }*/

        while (x >= 0 && x < boardSpaces.length && y >= 0 && y < boardSpaces[0].length) {
            Space space = boardSpaces[x][y];
            spacesHit.add(space);
            space.addLaserOnSpace(direction);

            // If we hit a player, that is not the owner
            Player playerOnSpace = space.getPlayer();
            if (playerOnSpace != null) {
                if (playerOnSpace != owner) {
                    break;
                }
            }

            // The next space is out of bounds OR there is a wall between this and the next space, break the loop.
            Space nextSpace = space.getSpaceNextTo(direction, boardSpaces);
            if (nextSpace == null || space.getIsWallBetween(nextSpace)) {
                break;
            }

            // Move to the next space
            x += dx;
            y += dy;
        }
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

    private synchronized void setIterationComplete(boolean value) {
        iterationComplete = value;
        notifyAll();
    }
}
