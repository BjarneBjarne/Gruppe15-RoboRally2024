package com.group15.roborally.client.model.events;

import com.group15.roborally.client.model.Command;

@FunctionalInterface
public interface PlayerCommandListener extends EventListener {
    /**
     * Called when any register is activated for any player.
     *
     * @return
     */
    Command onPlayerCommand(Command command);
}
