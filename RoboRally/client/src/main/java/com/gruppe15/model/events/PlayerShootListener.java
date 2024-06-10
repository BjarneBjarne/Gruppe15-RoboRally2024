package com.gruppe15.model.events;

import com.gruppe15.model.Laser;
import com.gruppe15.model.Player;

@FunctionalInterface
public interface PlayerShootListener extends EventListener {
    /**
     * Called when any player is about to shoot.
     *
     * @return
     */
    Laser onPlayerShoot(Player playerShooting, Laser laser);
}
