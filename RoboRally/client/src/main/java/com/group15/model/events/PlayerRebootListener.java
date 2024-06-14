package com.group15.model.events;

@FunctionalInterface
public interface PlayerRebootListener extends EventListener {
    /**
     * Called when any player is rebooting.
     *
     * @return
     */
    int onEvent();
}
