package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Placeholder BoardElement class.
 * TODO: Implement BoardElement class
 */
public abstract class BoardElement {
    private final Image image;
    private List<Wall> walls;

    /**
     * @param imageName Specified by the file name + the file extension. E.g: "empty.png".
     */
    public BoardElement(String imageName) {
        this.image = ImageUtils.getImageFromName(imageName);
    }

    public abstract boolean doAction(@NotNull GameController gameController, @NotNull Space space);

    public boolean getHasWall() {
        return !walls.isEmpty();
    }

    public List<Wall> getWalls() {
        return walls;
    }
    public Image getImage() {
        return image;
    }
}
