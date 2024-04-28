package gruppe15.roborally.model;

import gruppe15.roborally.model.upgrades.events.DamageDealtListener;

import java.util.ArrayList;
import java.util.List;

/**
 * This static class is for handling whenever an event takes place. Each "event" is defined as an extension of the EventListener.
 * <br>
 * Cards can subscribe to the event-listeners, so that they may trigger when said event takes place.
 * Players will then initialize the upgrade cards with themselves as owner, whenever they purchase a card.
 * <br>
 * For temporary cards, this may not include any behavior.
 */
public class EventHandler {
    private static final List<DamageDealtListener> DDListeners = new ArrayList<>();
    public static void setOnDamageDealt(DamageDealtListener DDListener) {
        DDListeners.add(DDListener);
    }
    public static void dealDamage(Player player, List<Player> targetList) {
        // Special behavior if player owns a damage card
        if (!DDListeners.isEmpty())
            for (DamageDealtListener listener : DDListeners) {
                listener.onDamageDealt(player, targetList);
            }
        // Normal damage behavior

    }
}
