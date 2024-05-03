package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.Wall;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Placeholder BoardElement class.
 * TODO: Implement BoardElement class
 */
public abstract class BoardElement {
    private final Image image;
    private final List<Wall> walls;

    public BoardElement(Image image, Heading direction, List<Wall> walls) {
        this.image = ImageUtils.getRotatedImageByHeading(image, direction);
        this.walls = walls;
    }
    /**
     * @param imageName Specified by the file name + the file extension. E.g: "empty.png".
     * @param walls
     */
    public BoardElement(String imageName, Heading direction, List<Wall> walls) {
        this(ImageUtils.getImageFromName(imageName), direction, walls);
    }
    public BoardElement(String imageName, List<Wall> walls) {
        this(ImageUtils.getImageFromName(imageName), Heading.NORTH, walls);
    }
    public BoardElement(String imageName, Heading direction) {
        this(ImageUtils.getImageFromName(imageName), direction, new ArrayList<>());
    }
    public BoardElement(String imageName) {
        this(imageName, Heading.NORTH, new ArrayList<>());
    }


    public abstract boolean doAction(@NotNull Space space, @NotNull Space[][] spaces);

    public boolean getHasWall() {
        return !walls.isEmpty();
    }

    public List<Wall> getWalls() {
        return walls;
    }
    public Image getImage() {
        return image;
    }

    public List<Heading> getWallDirections() {
        List<Heading> wallDirections = new ArrayList<>();
        for (Wall wall : walls) {
            wallDirections.add(wall.getDirection());
        }
        return wallDirections;
    }
}
