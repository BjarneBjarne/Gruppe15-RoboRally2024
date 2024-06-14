package com.group15.model.boardelements;

import com.group15.controller.GameController;
import com.group15.model.ActionWithDelay;
import com.group15.model.Player;
import com.group15.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * This class represents an energy space on the board and when a player is
 * on an energy space, the player gets an energy cube.
 * The energy space can only give an energy cube once.
 * If the player is on an energy space when the register is 5, the player gets
 * an energy cube.
 * 
 * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
 */
public class BE_EnergySpace extends BoardElement {
    private boolean hasEnergyCube = true;

    public BE_EnergySpace() {
        super("energySpace.png");
    }

    public boolean getHasEnergyCube() {
        return hasEnergyCube;
    }

    /**
     * When a player is on an energy space, the player gets an energy cube.
     * The energy space can only give an energy cube once.
     * If the player is on an energy space when the register is 5, the player gets
     * an energy cube because the player gets an energy cube in the last phase of
     * the register phase.
     * 
     * @param space          the space where the player is located
     * @param gameController the game controller
     * @param actionQueue    the queue of actions
     */
    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController,
            LinkedList<ActionWithDelay> actionQueue) {
        Player player = space.getPlayer();
        if (player != null) {
            if (hasEnergyCube) {
                hasEnergyCube = false;
                player.addEnergyCube();
            }
            if (gameController.board.getCurrentRegister() == 4) {
                player.addEnergyCube();
            }
        }
        return false;
    }
}
