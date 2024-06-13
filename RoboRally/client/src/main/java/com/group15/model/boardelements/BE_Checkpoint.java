package com.group15.model.boardelements;

import com.group15.controller.GameController;
import com.group15.model.ActionWithDelay;
import com.group15.model.Player;
import com.group15.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * This class represents a checkpoint on the board and when
 * a player reaches a checkpoint, the player's checkpoint number is updated.
 * 
 * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
 */
public class BE_Checkpoint extends BoardElement {
    public final int number;

    /**
     * Constructor for the checkpoint
     * @param number the number of the checkpoint
     * @param total the total number of checkpoints on the board
     */
    public BE_Checkpoint(int number) {
        super(number + ".png");
        if (number < 1 || 6 < number)
            throw new IllegalArgumentException("Invalid checkpoint number: " + number);
        this.number = number;
    }

    /**
     * When a player reaches a checkpoint, the player's checkpoint number is updated.
     * @param space the space where the player is located
     * @param gameController the game controller
     * @param actionQueue the queue of actions
     */
    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController, LinkedList<ActionWithDelay> actionQueue) {
        Player player = space.getPlayer();
        if (player == null)
            return false;
        if (player.getCheckpoints() == number - 1) {
            player.setCheckpoint(number);
            System.out.println(player.getName() + " has reached checkpoint " + number);
            gameController.checkpointReached(player, number);
        }
        return true;
    }
}
