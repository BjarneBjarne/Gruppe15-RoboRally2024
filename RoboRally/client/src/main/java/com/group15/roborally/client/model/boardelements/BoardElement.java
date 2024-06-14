package com.group15.model.boardelements;

import com.group15.roborally.controller.GameController;
import com.group15.model.ActionWithDelay;
import com.group15.model.Heading;
import com.group15.model.Space;
import com.group15.roborally.client.utils.ImageUtils;
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
    protected transient Image image;
    protected Heading direction;

    /**
     * Constructor for the board element
     *
     * @param imageName     the name of the image
     * @param direction the direction of the board element and the image
     */
    public BoardElement(String imageName, Heading direction) {
        setDirection(direction);
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
        if (direction == null) {
            this.image = ImageUtils.getImageFromName("Board Pieces/" + imageName);
        } else {
            this.image = ImageUtils.getRotatedImageByHeading(ImageUtils.getImageFromName("Board Pieces/" + imageName), direction);
        }
    }

    public Image getImage() {
        return image;
    }

    /**
     * Sets the direction of the board element and rotates the image according to
     * the direction of the board element.
     *
     * @param direction the direction of the board element
     */
    public void setDirection(Heading direction) {
        this.direction = direction;
        if (image != null) {
            setImage(image);
        }
    }

    /*public void updateImage() {
        setImage(imageName);
    }*/

    public Heading getDirection() {
        return direction;
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
