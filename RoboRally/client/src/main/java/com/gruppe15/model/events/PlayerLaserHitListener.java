package com.gruppe15.model.events;

import com.gruppe15.model.Player;
import com.gruppe15.model.damage.Damage;

@FunctionalInterface
public interface PlayerLaserHitListener extends EventListener {
    /**
     * Called when any player deals damage to one or more players.
     *
     * @param damage The damage to calculate.
     * @return The newly calculated damage.
     */
    Damage onPlayerDamage(Damage damage, Player playerTakingDamage);
}
