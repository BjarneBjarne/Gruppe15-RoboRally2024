package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.*;
import gruppe15.roborally.model.damage.Damage;
import gruppe15.roborally.model.damage.Spam;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BE_BoardLaser extends BoardElement {
    public BE_BoardLaser(Heading direction) {
        super("laserStart.png", direction);
    }

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
            Damage damage = new Damage();
            damage.setAmount(Spam.class, 1);
            for (Player playerHit : playersHit) {
                actionQueue.addFirst(new ActionWithDelay(() -> {
                    damage.applyDamage(playerHit);
                    System.out.println(playerHit.getName() + " hit by board laser!");
                }, Duration.millis(500)));
            }
        } catch (InterruptedException e) {
            // Handle InterruptedException
            System.out.println("Board laser interrupted: " + e.getMessage());
        }
    }

}
