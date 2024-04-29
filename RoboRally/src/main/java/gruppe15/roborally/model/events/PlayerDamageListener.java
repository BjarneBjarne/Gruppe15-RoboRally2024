package gruppe15.roborally.model.events;

import gruppe15.roborally.model.damage.Damage;
import gruppe15.roborally.model.upgrades.EventListener;

@FunctionalInterface
public interface PlayerDamageListener extends EventListener {
    /**
     * Called when any player deals damage to one or more players.
     *
     * @param damage The damage to calculate.
     * @return The newly calculated damage.
     */
    Damage onPlayerDamage(Damage damage);
}
