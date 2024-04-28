package gruppe15.roborally.model.upgrades.events;

import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.upgrades.EventListener;

public interface RebootListener extends EventListener {
    /**
     * Called when any player is rebooting.
     * <br>
     * @param player The player who is rebooting.
     */
    void onReboot(Player player);
}
