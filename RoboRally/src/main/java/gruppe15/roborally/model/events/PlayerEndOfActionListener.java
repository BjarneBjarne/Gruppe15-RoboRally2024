package gruppe15.roborally.model.events;

import gruppe15.roborally.model.Space;
import javafx.util.Pair;

@FunctionalInterface
public interface PlayerEndOfActionListener extends EventListener {
    /**
     * Called when any player is about to move.
     * @param space The space to calculate.
     * @return The newly calculated space.
     */
    boolean onEndOfAction(Space space, Boolean shouldReboot);
}
