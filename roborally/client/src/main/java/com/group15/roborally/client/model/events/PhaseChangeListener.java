package com.group15.roborally.client.model.events;

import com.group15.roborally.server.model.GamePhase;
import static com.group15.roborally.server.model.GamePhase.*;

@FunctionalInterface
public interface PhaseChangeListener {
    /**
     * Called when the board phase changes.
     * <br>
     * @param phase The phase changing to.
     */
    void onPhaseChange(GamePhase phase);
}
