package gruppe15.roborally.model.events;

import gruppe15.roborally.model.Phase;
import gruppe15.roborally.model.Player;

@FunctionalInterface
public interface PhaseChangeListener {
    /**
     * Called when the board phase changes.
     * <br>
     * @param phase The phase changing to.
     */
    void onPhaseChange(Phase phase);
}
