package com.group15.model.boardelements;

import com.group15.controller.GameController;
import com.group15.model.ActionWithDelay;
import com.group15.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * This class represents a hole on the board and when
 * a player reaches a hole, the player falls down the hole and is removed from
 * the board. The player is then respawned at the start of the board
 */
public class BE_Hole extends BoardElement {
    /**
     * @param imageName Specified by the file name + the file extension. E.g:
     *                  "empty.png".
     */
    public BE_Hole(String imageName) {
        super(imageName);
    }

    public BE_Hole() {
        super("hole.png");
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController,
            LinkedList<ActionWithDelay> actionQueue) {
        return false;
    }
}
