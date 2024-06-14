package com.group15.roborally.client.model.events;

import com.group15.roborally.client.model.Phase;

@FunctionalInterface
public interface PhaseChangeListener {
    /**
     * Called when the board phase changes.
     * <br>
     * @param phase The phase changing to.
     */
    void onPhaseChange(Phase phase);
}
