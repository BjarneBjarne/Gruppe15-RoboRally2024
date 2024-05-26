package gruppe15.roborally.model.events;

import gruppe15.roborally.model.Player;

@FunctionalInterface

public interface PlayerPushListener extends EventListener {
    /**
     * Called when any player pushes one or more players.
     *
     * @return
     */
    void onPush(Player playerPushing, Player playerToPush);
}
