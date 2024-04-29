package gruppe15.roborally.model;

/**
 * TODO: Implement BoardElement class
 */
public abstract class BoardElement {
    private final boolean hasWall;
    private final Heading wallDirection;
    public BoardElement(boolean hasWall, Heading wallDirection) {
        this.hasWall = hasWall;
        this.wallDirection = wallDirection;
    }
}
