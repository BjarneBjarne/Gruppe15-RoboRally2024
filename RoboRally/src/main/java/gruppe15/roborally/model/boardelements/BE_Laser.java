package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.model.*;
import gruppe15.roborally.model.damage.Damage;
import gruppe15.roborally.model.damage.Spam;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BE_Laser extends BoardElement {
    private final Heading direction;
    public BE_Laser(Heading direction) {
        super("laserStart.png", direction);
        this.direction = direction;
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull Space[][] spaces, LinkedList<ActionWithDelay> actionQueue) {
        actionQueue.addLast(new ActionWithDelay(() -> {
            // Clearing lasers in between board lasers
            for (int x = 0; x < spaces.length; x++) {
                for (int y = 0; y < spaces[x].length; y++) {
                    spaces[x][y].clearLasersOnSpace();
                }
            }
            Laser laser = new Laser(space, direction);
            // Start the laser iteration asynchronously
            laser.startLaser(spaces).run();
            // Once the laser iteration is complete, calculate the damage
            calculateDamage(space, laser, actionQueue);
        }, Duration.millis(350), "Board laser"));
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
                    System.out.println(playerHit.getName() + " hit by laser!");
                }, Duration.millis(1000)));
            }
        } catch (InterruptedException e) {
            // Handle InterruptedException
            System.out.println("Board laser interrupted: " + e.getMessage());
        }
    }

}
