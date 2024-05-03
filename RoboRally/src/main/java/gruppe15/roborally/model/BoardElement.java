package gruppe15.roborally.model;

import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;

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
        this.image = ImageUtils.getImageFromName(imageName);
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
    public Image getImage() {
        return image;
    }
}
