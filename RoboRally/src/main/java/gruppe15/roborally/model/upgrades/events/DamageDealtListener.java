package gruppe15.roborally.model.upgrades.events;

import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.upgrades.EventListener;

import java.util.List;

@FunctionalInterface
public interface DamageDealtListener extends EventListener {
    /**
     * Called when any player deals damage to one or more players.
     * <br>
     * @param player The player dealing damage.
     * @param targets The list of players targeted.
     */
    void onDamageDealt(Player player, List<Player> targets);
}
