package com.group15.roborally.client.model.events;

import com.group15.roborally.client.model.Space;
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
