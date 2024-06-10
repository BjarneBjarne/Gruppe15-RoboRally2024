package com.gruppe15.model.events;

import com.gruppe15.model.Command;

@FunctionalInterface
public interface PlayerCommandListener extends EventListener {
    /**
     * Called when any register is activated for any player.
     *
     * @return
     */
    Command onPlayerCommand(Command command);
}
