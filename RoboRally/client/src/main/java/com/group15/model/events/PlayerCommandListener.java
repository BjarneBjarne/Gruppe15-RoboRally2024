package com.group15.model.events;

import com.group15.model.Command;

@FunctionalInterface
public interface PlayerCommandListener extends EventListener {
    /**
     * Called when any register is activated for any player.
     *
     * @return
     */
    Command onPlayerCommand(Command command);
}
