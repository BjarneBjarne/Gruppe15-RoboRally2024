package com.group15.model.boardelements;

import com.group15.controller.GameController;
import com.group15.model.ActionWithDelay;
import com.group15.model.Heading;
import com.group15.model.Player;
import com.group15.model.Space;
import com.group15.utils.ImageUtils;
import javafx.scene.paint.Color;
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

    public void setColor(Player player) {
        Color playerColor = Color.valueOf(player.getRobot().name());
        this.image = ImageUtils.getImageColored(this.image, playerColor, .75);
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController,
            LinkedList<ActionWithDelay> actionQueue) {
        return false;
    }
}
