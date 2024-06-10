package com.gruppe15.model.events;

import com.gruppe15.model.Phase;

@FunctionalInterface
public interface PhaseChangeListener {
    /**
     * Called when the board phase changes.
     * <br>
     * @param phase The phase changing to.
     */
    void onPhaseChange(Phase phase);
}
