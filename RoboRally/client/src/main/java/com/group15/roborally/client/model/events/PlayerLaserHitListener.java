package com.group15.model.events;

import com.group15.roborally.server.model.Player;
import com.group15.model.damage.Damage;

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
