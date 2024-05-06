package gruppe15.roborally.model;

import gruppe15.roborally.model.boardelements.BE_Hole;
import gruppe15.roborally.model.boardelements.BoardElement;
import gruppe15.roborally.model.damage.Damage;
import gruppe15.roborally.model.damage.DamageType;
import gruppe15.roborally.model.damage.Spam;
import gruppe15.roborally.model.events.PlayerCommandListener;
import gruppe15.roborally.model.events.PlayerDamageListener;
import gruppe15.roborally.model.events.PlayerMoveListener;
import gruppe15.roborally.model.events.PlayerPushListener;
import gruppe15.roborally.model.upgrades.EventListener;
import javafx.util.Duration;
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

    /**
     * Generic hashmap for EventListeners/Cards.
     */
    private static final Map<Class<? extends EventListener>, Map<EventListener, Player>> listeners = new HashMap<>();

    /**
     * Subscription method for EventListeners/Cards.
     * @param listenerType
     * @param listener
     * @param owner
     */
    public static void onEvent(Class<? extends EventListener> listenerType, EventListener listener, Player owner) {
        listeners.computeIfAbsent(listenerType, k -> new HashMap<>()).put(listener, owner);
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




    /**
     * Method for whenever a player shoots.
     * @param spaces
     * @param playerShooting
     * @param actionQueue
     */
    public static void event_PlayerShoot(Space[][] spaces, Player playerShooting, LinkedList<ActionWithDelay> actionQueue) {
        actionQueue.addLast(new ActionWithDelay(() -> {
            // Clearing lasers in between player lasers
            for (int x = 0; x < spaces.length; x++) {
                for (int y = 0; y < spaces[x].length; y++) {
                    spaces[x][y].clearLasersOnSpace();
                }
            }
            // Create a laser object
            Laser laser = new Laser(playerShooting.getSpace(), playerShooting.getHeading(), playerShooting);
            // Start the laser iteration asynchronously
            laser.startLaser(spaces).run();

            // Wait for the laser iteration to complete and calculate the damage
            try {
                List<Player> playersHit = calculatePlayersHit(laser);
                // Deal damage to each target player
                for (Player target : playersHit) {
                    if (target == playerShooting) {
                        continue;
                    }
                    Damage damage = new Damage();
                    damage.setAmount(Spam.class, 1);

                    // Apply any modifications to damage based on player's cards
                    List<PlayerDamageListener> playerPlayerDamageListeners = getPlayerCardEventListeners(playerShooting, PlayerDamageListener.class);
                    for (PlayerDamageListener listener : playerPlayerDamageListeners) {
                        damage = listener.onPlayerDamage(damage);
                    }

                    // Apply damage to the target player
                    for (Map.Entry<Class<? extends DamageType>, DamageType> entry : damage.getDamageMap().entrySet()) {
                        if (entry.getValue().getAmount() > 0) {
                            damage.applyDamage(target);
                            Damage finalDamage = damage;
                            actionQueue.addFirst(new ActionWithDelay(() -> {
                                // Print the damage dealt
                                System.out.println("{" + playerShooting.getName() + "} dealt " + finalDamage.getAmount(entry.getKey()) + " " + entry.getValue().getAmount() + " damage to {" + target.getName() + "}");
                            }, Duration.millis(500)));
                        }
                    }
                }
            } catch (InterruptedException e) {
                // Handle InterruptedException
                System.out.println("Player laser interrupted: " + e.getMessage());
            }
        }, Duration.millis(250), "Player laser"));
    }
    private static List<Player> calculatePlayersHit(Laser laser) throws InterruptedException {
        List<Player> playersHit = new ArrayList<>();
        // Wait for the laser iteration to complete and get the spaces hit
        for (Space space : laser.getSpacesHit()) {
            Player target = space.getPlayer();
            if (target != null) {
                playersHit.add(target);
            }
        }
        return playersHit;
    }




    /**
     * Method for when a register is activated.
     * @param playerActivatingRegister
     * @param command
     * @return
     */
    public static Command event_RegisterActivate(@NotNull Player playerActivatingRegister, Command command) {
        List<PlayerCommandListener> playerCommandListeners = getPlayerCardEventListeners(playerActivatingRegister, PlayerCommandListener.class);
        for (PlayerCommandListener listener : playerCommandListeners) {
            command = listener.onPlayerCommand(command);
        }
        return command;
    }




    /**
     * Method for when players are pushed.
     * @param spaces
     * @param playerPushing
     * @param playersToPush
     * @param pushDirection
     */
    public static void event_PlayerPush(Space[][] spaces, Player playerPushing, List<Player> playersToPush, Heading pushDirection) {
        for (Player playerToPush : playersToPush) {
            if (playerToPush == playerPushing) {
                continue;
            }
            List<PlayerPushListener> playerPushListeners = getPlayerCardEventListeners(playerPushing, PlayerPushListener.class);
            for (PlayerPushListener listener : playerPushListeners) {
                listener.onPush(playerToPush);
            }
            // Set players new position
            playerToPush.setSpace(playerToPush.getSpace().getSpaceNextTo(pushDirection, spaces));
            // TODO: Make playerOnSpace (fall off / reboot)
        }
    }




    /**
     * Method for when a player moves. This should only be called when a player moves without being pushed.
     */
    public static void event_PlayerMove(Player playerMoving, Space space) {
        List<PlayerMoveListener> playerMoveListeners = getPlayerCardEventListeners(playerMoving, PlayerMoveListener.class);

        // Handle listener logic
        for (PlayerMoveListener listener : playerMoveListeners) {
            space = listener.onPlayerMove(space);
        }

        // If no listeners, handle base logic
        if (playerMoveListeners.isEmpty()) {
            BoardElement spaceBoardElement = space.getBoardElement();
            if (spaceBoardElement instanceof BE_Hole) {
                // TODO: Handle rebooting player since they fell down a hole.
            }
        }

        playerMoving.setSpace(space);
    }

}
