package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.*;
import gruppe15.roborally.model.damage.Damage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        Laser laser = new Laser(space, direction);
        // Start the laser iteration asynchronously
        laser.startLaser(spaces).run();
        // Once the laser iteration is complete, calculate the damage
        calculateDamage(space, laser, actionQueue);
        return true;
    }

    /**
     * This method calculates the damage to the players hit by the board laser.
     * 
     * @param space       the space the board laser is on
     * @param laser       the laser object
     * @param actionQueue the action queue
     */
    private void calculateDamage(Space space, Laser laser, LinkedList<ActionWithDelay> actionQueue) {
        // Calculate players on the laser synchronously
        try {
            List<Player> playersHit = new ArrayList<>();
            for (Space ignored : laser.getSpacesHit()) {
                Player target = space.getPlayer();
                if (target != null) {
                    playersHit.add(target);
                }
            }
            Damage damage = new Damage(1, 0, 0, 0);
            for (Player playerHit : playersHit) {
                actionQueue.addFirst(new ActionWithDelay(() -> {
                    damage.applyDamage(playerHit, null);
                    System.out.println(playerHit.getName() + " hit by board laser!");
                }, Duration.millis(500)));
            }
        } catch (InterruptedException e) {
            // Handle InterruptedException
            System.out.println("Board laser interrupted: " + e.getMessage());
        }
    }
}
