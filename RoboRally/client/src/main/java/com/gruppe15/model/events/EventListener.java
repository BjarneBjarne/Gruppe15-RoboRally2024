package com.gruppe15.model.events;

/**
 * A tagging interface that all event listener interfaces must extend.
 * Listeners should be made, when a card depends on a specific event trigger.
 * <br><br>
 * Interfaces extended from this, must define the event that should be listened to, for upgrade card's effects to take place.
 * <br>
 * For permanent cards, this typically includes most of their behavior.
 * <br>
 * For temporary cards, this may not include any behavior.
 */
public interface EventListener {
}
