package gruppe15.roborally.model.upgrades.events;

import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.upgrades.EventListener;

public interface MoveStartListener extends EventListener {
    /**
     * Called when any player is about to move.
     * <br>
     * @param player The player who is about to move.
     */
    void onMoveStart(Player player);
}
