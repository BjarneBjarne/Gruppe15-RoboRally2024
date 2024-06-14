package com.group15.model.boardelements;

import com.group15.controller.GameController;
import com.group15.model.ActionWithDelay;
import com.group15.model.Heading;
import com.group15.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * This class represents a reboot on the board and when a player is
 * rebooted, the player is rebooted on the reboot.
 * 
 * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
 */
public class BE_Reboot extends BoardElement {
    public BE_Reboot(Heading direction) {
        super("reboot.png", direction);
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController, LinkedList<ActionWithDelay> actionQueue) {
        return false;
    }
}
