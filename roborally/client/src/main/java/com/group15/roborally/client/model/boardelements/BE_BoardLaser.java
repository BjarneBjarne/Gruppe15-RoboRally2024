package com.group15.roborally.client.model.boardelements;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.damage.Damage;
import com.group15.roborally.client.model.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.group15.roborally.client.LobbySettings.STANDARD_DAMAGE;

/**
 * This class represents a board laser on the board and when a player is hit by
 * the laser, the player takes damage. The laser iterates through the board
 * spaces in the direction of the laser and hits players on the way.
 * 
 * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
 */
public class BE_BoardLaser extends BoardElement {
    /**
     * Constructor for the board laser element
     * 
     * @param direction the direction of the board laser
     */
    public BE_BoardLaser(Heading direction) {
        super("boardLaser.png", direction);
    }

    /**
     * This method is called when a player is hit by the board laser. The method
     * starts the laser iteration and calculates the damage to the players hit by
     * the laser.
     * 
     * @param space          the space the board laser is on
     * @param gameController the game controller
     * @param actionQueue    the action queue
     * @return true if the action was successful, false otherwise
     */
    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController, LinkedList<ActionWithDelay> actionQueue) {
        Space[][] spaces = gameController.board.getSpaces();
        Laser laser = new Laser(space, direction, null, Player.class, Space.class);
        // Start the laser iteration asynchronously
        laser.startLaser(spaces).run();
        // Once the laser iteration is complete, calculate the damage
        dealDamage(laser, actionQueue);
        return true;
    }

    /**
     * This method deals damage to the players hit by the board laser.
     * @param laser       the laser object
     * @param actionQueue the action queue
     */
    private void dealDamage(Laser laser, LinkedList<ActionWithDelay> actionQueue) {
        try {
            List<Player> playersHit = calculatePlayersHit(laser);
            // Deal damage to each target player
            for (Player playerHit : playersHit) {
                Damage damage = new Damage(0, 0, 0, 0);
                damage.add(STANDARD_DAMAGE);
                actionQueue.addFirst(new ActionWithDelay(() -> {
                    damage.applyDamage(playerHit, null);
                    System.out.println(playerHit.getName() + " hit by board laser!");
                }, 500));
            }
        } catch (InterruptedException e) {
            // Handle InterruptedException
            System.out.println("Player laser interrupted: " + e.getMessage());
        }
    }

    private static List<Player> calculatePlayersHit(Laser laser) throws InterruptedException {
        List<Player> playersHit = new ArrayList<>();
        // Wait for the laser iteration to complete and get the spaces hit
        for (Space space : laser.getSpacesHit()) {
            Player target = space.getPlayer();
            if (target != null) {
                if (!playersHit.contains(target)) {
                    playersHit.add(target);
                }
            }
        }
        return playersHit;
    }
}
