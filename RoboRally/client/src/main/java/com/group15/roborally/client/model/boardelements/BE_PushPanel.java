package com.group15.roborally.client.model.boardelements;

import java.util.LinkedList;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.ActionWithDelay;
import com.group15.roborally.client.model.Heading;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.Space;

/**
 * This class represents a push panel on the board and when
 * a player reaches a push panel, the player is pushed to the next space
 * in the direction of the push panel.
 * 
 * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
 */
public class BE_PushPanel extends BoardElement {

    // The registers that the push panel pushes the player to
    public final int[] pushRegisters;

    /**
     * Constructor for the push panel
     * 
     * @param type      the type of the push panel
     * @param direction the direction of the push panel
     */
    public BE_PushPanel(String type, Heading direction) {
        super("push" + type + ".png", direction);
        if (type != "24" && type != "135")
            throw new IllegalArgumentException("Invalid direction: " + type);
        if (type == "24") {
            this.pushRegisters = new int[] { 2, 4 };
        } else if (type == "135") {
            this.pushRegisters = new int[] { 1, 3, 5 };
        } else {
            throw new IllegalArgumentException("Invalid direction: " + type);
        }
        setDirection(direction.next().next());
    }

    /**
     * Checks if an array contains a target
     * 
     * @param arr    the array
     * @param target the target
     * @return true if the array contains the target, false otherwise
     */
    private static boolean contains(int[] arr, int target) {
        for (int i : arr) {
            if (i == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * When a player reaches a push panel, the player is pushed to the next space
     * in the direction of the push panel.
     * 
     * @param space          the space where the player is located
     * @param gameController the game controller
     * @param actionQueue    the queue of actions
     */
    @Override
    public boolean doAction(Space space, GameController gameController, LinkedList<ActionWithDelay> actionQueue) {
        Player player = space.getPlayer();
        if (player == null) return false;
        int currentRegister = gameController.board.getCurrentRegister();
        if (contains(pushRegisters, currentRegister + 1)) {
            gameController.board.movePlayerToSpace(player, space.getSpaceNextTo(direction, gameController.board.getSpaces()), gameController);
        }
        return true;
    }
}
