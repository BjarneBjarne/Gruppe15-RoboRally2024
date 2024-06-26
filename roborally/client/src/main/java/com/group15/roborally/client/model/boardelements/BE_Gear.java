package com.group15.roborally.client.model.boardelements;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.ActionWithDelay;
import com.group15.roborally.client.model.Heading;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;

/**
 * This class represents a gear on the board and when
 * a player reaches a gear, the player's heading is updated
 * according to the direction of the gear.
 * The gear can turn the player either left or right.
 * 
 * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
 */
public class BE_Gear extends BoardElement {

    private final String rotateDirection;

/**
     * Constructor for the gear
     * 
     * @param rotateDirection the direction of the gear
     */
  
    public BE_Gear(String rotateDirection) {
        super("gear" + rotateDirection + ".png");
        if (!rotateDirection.equals("Right") && !rotateDirection.equals("Left"))
            throw new IllegalArgumentException("Invalid direction: " + rotateDirection);
        this.rotateDirection = rotateDirection;
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
        if (Objects.equals(this.rotateDirection, "Right"))
            newHeading = player.getHeading().next();
        else if (Objects.equals(this.rotateDirection, "Left"))
            newHeading = player.getHeading().prev();
        else
            return false;
        player.setHeading(newHeading);
        return true;
    }
}
