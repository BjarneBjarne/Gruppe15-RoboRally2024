package gruppe15.roborally.model;

/**
 * Placeholder BoardElement class.
 * TODO: Implement BoardElement class
 */
public abstract class BoardElement {
    private final boolean hasWall;
    private final boolean isHole;
    private final Heading wallDirection;
    public BoardElement(boolean hasWall, boolean isHole, Heading wallDirection) {
        this.hasWall = hasWall;
        this.isHole = isHole;
        this.wallDirection = wallDirection;
    }

    public boolean getHasWall() {
        return hasWall;
    }
    public boolean getIsHole() {
        return isHole;
    }
    public Heading getWallDirection() {
        return wallDirection;
    }
}
