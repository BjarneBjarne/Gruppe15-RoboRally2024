package com.group15.model.events;

import com.group15.model.Player;
import com.group15.model.damage.Damage;

@FunctionalInterface

public interface PlayerPushListener extends EventListener {
    /**
     * Called when any player pushes one or more players.
     *
     * @return
     */
    Damage onPush(Player playerPushing, Player playerToPush, Damage damage);
}
