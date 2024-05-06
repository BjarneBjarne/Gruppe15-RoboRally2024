package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * Placeholder BoardElement class.
 * TODO: Implement BoardElement class
 */
public abstract class BoardElement {
    private String imageName = "";
    private Image image;

    public BoardElement(String imageName, Heading direction) {
        setImage(imageName, direction);
    }
    /**
     * @param imageName Specified by the file name + the file extension. E.g: "empty.png".
     */
    public BoardElement(String imageName) {
        setImage(imageName);
    }

    public BoardElement() { }

    public void setImage(String imageName) {
        if (!imageName.isEmpty()) {
            this.imageName = imageName;
            this.image = ImageUtils.getImageFromName(imageName);
        }
    }
    public void setImage(String imageName, Heading direction) {
        if (!imageName.isEmpty()) {
            this.imageName = imageName;
            this.image = ImageUtils.getRotatedImageByHeading(ImageUtils.getImageFromName(imageName), direction);
        }
    }

    public void calculateImage(int x, int y, Space[][] spaces){}

    public abstract boolean doAction(@NotNull Space space, @NotNull Board board, LinkedList<ActionWithDelay> actionQueue);

    public Image getImage() {
        return image;
    }
}
