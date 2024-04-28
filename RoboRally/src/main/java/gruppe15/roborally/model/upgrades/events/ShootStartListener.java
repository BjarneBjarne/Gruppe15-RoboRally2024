package gruppe15.roborally.model.upgrades.events;

import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.upgrades.EventListener;

public interface ShootStartListener extends EventListener {
    /**
     * Called when any player is about to shoot.
     * <br>
     * @param player The player who is about to shoot.
     */
    void onShootStart(Player player);
}
