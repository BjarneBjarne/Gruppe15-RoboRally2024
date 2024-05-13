package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * This class represents a board element on the board.
 * The board element is the superclass of all the different board elements on
 * the board. The board element has an image and a direction.
 * 
 * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
 */
public abstract class BoardElement {
    protected Image image;
    protected Heading elemDirection;

    /**
     * Constructor for the board element
     * 
     * @param imageName     the name of the image
     * @param elemDirection the direction of the board element and the image
     */
    public BoardElement(String imageName, Heading elemDirection) {
        setElemDirection(elemDirection);
        setImage(imageName);
    }

    /**
     * Constructor for the board element
     * 
     * @param imageName the name of the image
     */
    public BoardElement(String imageName) {
        setImage(imageName);
    }

    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Sets the image of the board element to the image with the given name and
     * rotates the image according to the direction of the board element.
     * 
     * @param imageName the name of the image
     */
    public void setImage(String imageName) {
        if (imageName == null || imageName.isEmpty())
            return;
        if (elemDirection == null) {
            this.image = ImageUtils.getImageFromName(imageName);
        } else {
            this.image = ImageUtils.getRotatedImageByHeading(ImageUtils.getImageFromName(imageName), elemDirection);
        }
    }

    public Image getImage() {
        return image;
    }

    /**
     * Sets the direction of the board element and rotates the image according to
     * the direction of the board element.
     * 
     * @param elemDirection the direction of the board element
     */
    public void setElemDirection(Heading elemDirection) {
        this.elemDirection = elemDirection;
        if (image != null) {
            setImage(image);
        }
    }

    public Heading getElemDirection() {
        return elemDirection;
    }

    /**
     * Performs the action of the board element.
     * 
     * @param space          the space where the player is located
     * @param gameController the game controller
     * @param actionQueue    the queue of actions
     * @return true if the action was performed, false otherwise
     */
    public abstract boolean doAction(@NotNull Space space, @NotNull GameController gameController,
            LinkedList<ActionWithDelay> actionQueue);
}
