package gruppe15.roborally.model.events;

import gruppe15.roborally.model.Laser;
import gruppe15.roborally.model.Player;

@FunctionalInterface
public interface PlayerShootListener extends EventListener {
    /**
     * Called when any player is about to shoot.
     *
     * @return
     */
    Laser onPlayerShoot(Player playerShooting, Laser laser);
}
