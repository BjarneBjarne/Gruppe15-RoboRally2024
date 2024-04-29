package gruppe15.roborally.model.events;

import gruppe15.roborally.model.upgrades.EventListener;

@FunctionalInterface
public interface PlayerRebootListener extends EventListener {
    /**
     * Called when any player is rebooting.
     *
     * @return
     */
    int onEvent();
}
