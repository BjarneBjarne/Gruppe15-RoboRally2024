package gruppe15.roborally.model;

import javafx.scene.image.Image;

import java.util.Objects;

/**
 * Placeholder BoardElement class.
 * TODO: Implement BoardElement class
 */
public class BoardElement {
    private final boolean hasWall;
    private final boolean isHole;
    private final Heading wallDirection;
    private final Image image;

    /**
     *
     * @param hasWall
     * @param isHole
     * @param wallDirection
     * @param imageName Specified by the file name + the file extension. E.g: "empty.png".
     */
    public BoardElement(boolean hasWall, boolean isHole, Heading wallDirection, String imageName) {
        this.hasWall = hasWall;
        this.isHole = isHole;
        this.wallDirection = wallDirection;
        this.image = getImageFromName(imageName);
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

    private Image getImageFromName(String imageName) {
        String imagePath = "/gruppe15/roborally/images/" + imageName;
        try {
            return new Image(Objects.requireNonNull(BoardElement.class.getResourceAsStream(imagePath)));
        } catch (Exception e) {
            System.out.println("Error importing image with path: " + imagePath);
            return null;
        }
    }
    public Image getImage() {
        return image;
    }
}
