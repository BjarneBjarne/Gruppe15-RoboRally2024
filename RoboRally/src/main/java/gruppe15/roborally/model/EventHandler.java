package gruppe15.roborally.model;

import gruppe15.roborally.model.damage.Damage;
import gruppe15.roborally.model.events.PlayerCommandListener;
import gruppe15.roborally.model.events.PlayerShootListener;
import gruppe15.roborally.model.upgrades.EventListener;
import gruppe15.roborally.model.events.PlayerDamageListener;
import gruppe15.roborally.model.damage.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * This static class is for handling whenever an event takes place. Each "event" is defined as an extension of the EventListener.
 * <br>
 * Cards can subscribe to the event-listeners, so that they may trigger when said event takes place.
 * Players will then initialize the upgrade cards with themselves as owner, whenever they purchase a card.
 * Cards then need to verify, that the event happening, is for the player owning that card.
 * (This could be done with simpler logic, but makes it possible for cards to be isolated and handle their behavior,
 * without having to call the card logic when the card owner initiates the associated event.)
 * <br>
 * For permanent cards, this includes most of their behavior, since it should happen automatically under their subscribed events.
 * For temporary cards, this may not include any behavior.
 */
public class EventHandler {
    // Generic hashmap and subscription method for all EventListener's/Cards.
    private static final Map<Class<? extends EventListener>, Map<EventListener, Player>> listeners = new HashMap<>();

    public static void onEvent(Class<? extends EventListener> listenerType, EventListener listener, Player owner) {
        listeners.computeIfAbsent(listenerType, k -> new HashMap<>()).put(listener, owner);
    }

    // Method for whenever a player shoots
    public static void event_PlayerShoot(Board board, Player player) {
        // Get targets
        List<Player> targets = getTargetsOnPlayerLaser(player, board.getSpaces());

        // Deal damage to each target
        for (Player target : targets) {
            if (target == player) {
                continue;
            }
            Damage damage = new Damage(); // List of different damage the target is going to take.
            damage.setAmount(Spam.class, 1); // Normal damage is 1 SPAM.

            // Get a list of all the listeners in cards of type PlayerDamageListener that is owned by the player.
            List<PlayerDamageListener> playerPlayerDamageListeners = getPlayerCardEventListeners(player, PlayerDamageListener.class);
            // Iterate through all the player's "damage cards" to modify the damage for each of them.
            for (PlayerDamageListener listener : playerPlayerDamageListeners) {
                // Pass the damage to the listener and get the newly calculated damage
                damage = listener.onPlayerDamage(damage);
            }

            // Standard damage behavior
            for (Map.Entry<Class<? extends DamageType>, DamageType> entry : damage.getDamageMap().entrySet()) {
                damage.applyDamage(target);
                // Print the damage dealt
                System.out.println("{" + player.getName() + "} dealt " + damage.getAmount(entry.getKey()) + " " + entry.getKey().getName() + " damage to {" + target.getName() + "}");
            }
        }
    }

    // Method for when a register is activated
    public static Command event_RegisterActivate(Board board, @NotNull Player player, Command command) {
        List<PlayerCommandListener> playerCommandListeners = getPlayerCardEventListeners(player, PlayerCommandListener.class);
        for (PlayerCommandListener listener : playerCommandListeners) {
            command = listener.onPlayerCommand(command);
        }
        return command;
    }







    private static List<Player> getTargetsOnPlayerLaser(Player player, Space[][] boardSpaces) {
        Laser laser = new Laser(player.getSpace(), player.getHeading(), boardSpaces);
        List<PlayerShootListener> playerPlayerShootListeners = getPlayerCardEventListeners(player, PlayerShootListener.class);
        for (PlayerShootListener listener : playerPlayerShootListeners) {
            // Pass the laster to any listeners and get spaces
            laser = listener.onPlayerShoot(laser);
        }

        return getPlayersOnLaser(laser);
    }

    private static List<Player> getPlayersOnLaser(Laser laser) {
        // Calculate players on a laser
        List<Player> players = new ArrayList<>();
        for (Space space : laser.getSpacesHit()) {
            Player target = space.getPlayer();
            if (target != null) {
                players.add(target);
            }
        }
        return players;
    }

    /**
     * When a card has been bought, it gets initialized where it subscribes to "listeners" with the player buying it set as owner in the hashmap.
     * This method goes through all the cards that have been bought and looks for EventListeners in those cards that match the given type.
     * If the player who initiated the event is the owner of the card, the EventListener in the card is added to the getPlayerCardEventListeners() list.
     * @param player The player who owns the card(s).
     * @param eventListenerType The type of event that is listened to by cards.
     * @return Returns the EventListeners of a type in the cards owned by the player.
     */
    private static <T extends EventListener> List<T> getPlayerCardEventListeners(Player player, Class<T> eventListenerType) {
        List<T> playerEventListeners = new ArrayList<>();
        Map<EventListener, Player> eventListeners = listeners.getOrDefault(eventListenerType, Collections.emptyMap());
        // Iterate through all cards/listeners
        for (Map.Entry<EventListener, Player> entry : eventListeners.entrySet()) {
            EventListener listener = entry.getKey();
            Player owner = entry.getValue();
            // Check if the player is the owner of the card and the listener is of the specified type
            if (owner == player && eventListenerType.isInstance(listener)) {
                playerEventListeners.add(eventListenerType.cast(listener));
            }
        }
        return playerEventListeners;
    }

}
