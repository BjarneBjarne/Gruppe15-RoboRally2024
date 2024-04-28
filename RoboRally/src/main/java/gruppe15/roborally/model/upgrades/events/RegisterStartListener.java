package gruppe15.roborally.model.upgrades.events;

import gruppe15.roborally.model.upgrades.EventListener;

public interface RegisterStartListener extends EventListener {
    /**
     * Called when a new register starts in the Activation Phase.
     */
    void onRegisterStart();
}
