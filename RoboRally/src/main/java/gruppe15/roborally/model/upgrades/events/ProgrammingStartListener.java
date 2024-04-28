package gruppe15.roborally.model.upgrades.events;

import gruppe15.roborally.model.upgrades.EventListener;

public interface ProgrammingStartListener extends EventListener {
    /**
     * Called when the programming phase begins.
     */
    void onProgrammingStart();
}
