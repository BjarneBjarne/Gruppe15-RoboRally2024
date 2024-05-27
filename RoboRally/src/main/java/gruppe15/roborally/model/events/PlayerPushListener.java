package gruppe15.roborally.model.events;

import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.damage.Damage;

@FunctionalInterface

public interface PlayerPushListener extends EventListener {
    /**
     * Called when any player pushes one or more players.
     *
     * @return
     */
    Damage onPush(Player playerPushing, Player playerToPush, Damage damage);
}
