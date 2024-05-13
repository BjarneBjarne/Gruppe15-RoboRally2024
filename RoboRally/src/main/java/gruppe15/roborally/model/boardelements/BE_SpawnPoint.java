package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * This class represents a spawn point on the board and when a player is
 * rebooted, the player is rebooted on the spawn point.
 */
public class BE_SpawnPoint extends BoardElement {
    /**
     * Constructor for the spawn point
     * 
     * @param direction The direction players should be pushed, if they stand on
     *                  this, while someone is rebooted here.
     */
    public BE_SpawnPoint(Heading direction) {
        super("startField.png", direction);
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController,
            LinkedList<ActionWithDelay> actionQueue) {
        return false;
    }
}
