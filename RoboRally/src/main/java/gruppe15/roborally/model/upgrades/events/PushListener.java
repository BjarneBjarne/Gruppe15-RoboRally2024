package gruppe15.roborally.model.upgrades.events;

import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.upgrades.EventListener;

import java.util.List;

public interface PushListener extends EventListener {
    /**
     * Called when any player pushes one or more players.
     * <br>
     * @param player The player pushing.
     * @param targets The list of players pushed by the player.
     */
    void onPush(Player player, List<Player> targets);
}
