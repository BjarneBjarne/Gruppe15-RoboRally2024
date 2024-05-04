package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

/**
 * Placeholder BoardElement class.
 * TODO: Implement BoardElement class
 */
public abstract class BoardElement {
    transient private final Image image;

    public BoardElement(Image image, Heading direction) {
        this.image = ImageUtils.getRotatedImageByHeading(image, direction);
    }
    /**
     * @param imageName Specified by the file name + the file extension. E.g: "empty.png".
     */
    public BoardElement(String imageName) {
        this.image = ImageUtils.getImageFromName(imageName);
    }

    public abstract boolean doAction(@NotNull Space space, @NotNull Space[][] spaces);

    public Image getImage() {
        return image;
    }
}
