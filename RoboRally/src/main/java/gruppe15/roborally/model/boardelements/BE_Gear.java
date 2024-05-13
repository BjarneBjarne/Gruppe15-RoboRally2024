package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;

/**
 * This class represents a gear on the board and when
 * a player reaches a gear, the player's heading is updated
 * according to the direction of the gear.
 * The gear can turn the player either left or right.
 * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
 */
public class BE_Gear extends BoardElement {

    private final String direction;

    /**
     * Constructor for the gear
     * 
     * @param direction the direction of the gear
     */
    public BE_Gear(String direction) {
        if (!Objects.equals(direction, "Right") && !Objects.equals(direction, "Left"))
            throw new IllegalArgumentException("Invalid direction: " + direction);
        this.direction = direction;
        setImage("gear" + this.direction + ".png");
    }

    /**
     * When a player reaches a gear, the player's heading is updated
     * according to the direction of the gear. The gear can turn the player
     * either left or right.
     * @param space the space where the player is located
     * @param gameController the game controller
     * @param actionQueue the queue of actions
     */
    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController, LinkedList<ActionWithDelay> actionQueue) {
        Player player = space.getPlayer();
        if (player == null)
            return false;
        Heading newHeading;
        if (Objects.equals(this.direction, "Right"))
            newHeading = player.getHeading().next();
        else if (Objects.equals(this.direction, "Left"))
            newHeading = player.getHeading().prev();
        else
            return false;
        player.setHeading(newHeading);
        return true;
    }

}
