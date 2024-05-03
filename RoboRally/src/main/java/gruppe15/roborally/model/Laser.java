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
    public Laser(Space origin, Heading direction, Space[][] boardSpaces) {
        this.origin = origin;
        this.direction = direction;
        calculateBaseSpacesHit(boardSpaces);
    }

    private void calculateBaseSpacesHit(Space[][] boardSpaces) {
        int dx = direction == Heading.WEST ? -1 : direction == Heading.EAST ? 1 : 0;
        int dy = direction == Heading.NORTH ? -1 : direction == Heading.SOUTH ? 1 : 0;

        int x = origin.x;
        int y = origin.y;
        while (x >= 0 && x < boardSpaces.length && y >= 0 && y < boardSpaces[0].length) {
            Space space = boardSpaces[x][y];
            // If next space is out of bounds or there is a wall between this and the next space, break the loop.
            Space nextSpace = space.getSpaceNextTo(direction, boardSpaces);
            if (nextSpace == null || space.getIsWallBetween(nextSpace)) {
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

    public List<Space> getSpacesHit() {
        return spacesHit;
    }

    public void addSpacesHit(List<Space> newSpacesHit) {
        for (Space space : newSpacesHit) {
            if (!spacesHit.contains(space)) {
                spacesHit.add(space);
            }
        }
    }
}
