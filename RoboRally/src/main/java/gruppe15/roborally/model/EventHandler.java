package gruppe15.roborally.model;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.boardelements.BE_Hole;
import gruppe15.roborally.model.boardelements.BE_Reboot;
import gruppe15.roborally.model.damage.Damage;
import gruppe15.roborally.model.damage.DamageTypeAmount;
import gruppe15.roborally.model.events.EventListener;
import gruppe15.roborally.model.events.*;
import javafx.util.Duration;
import javafx.util.Pair;
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
    private static final Map<EventListener, Player> listeners = new HashMap<>();

    /**
     * Subscription method for EventListeners/Cards.
     * @param listener
     * @param owner
     */
    public static EventListener subscribe(EventListener listener, Player owner) {
        listeners.put(listener, owner);
        return listener;
    }
    /**
     * Unsubscription method for EventListeners/Cards.
     * @param listener
     * @param owner
     */
    public static void unsubscribe(EventListener listener, Player owner) {
        listeners.entrySet().removeIf(entry -> entry.getKey().equals(listener) && entry.getValue().equals(owner));
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
        // Iterate through all cards/listeners
        for (Map.Entry<EventListener, Player> entry : listeners.entrySet()) {
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
     * @param board
     * @param playerShooting
     * @param actionQueue
     */
    public static void event_PlayerShoot(Board board, Player playerShooting, LinkedList<ActionWithDelay> actionQueue) {
        if (playerShooting.getIsRebooting()) {
            return;
        }
        // Clearing lasers in between player lasers
        board.clearLasers();

        Laser laser = new Laser(playerShooting.getSpace(), playerShooting.getHeading(), playerShooting);
        laser.startLaser(board.getSpaces()).run();
        try {
            List<Player> playersHit = calculatePlayersHit(laser);
            // Deal damage to each target player
            for (Player target : playersHit) {
                if (target == playerShooting) {
                    continue;
                }
                Damage damage = new Damage(1, 0, 0, 0);

                // Apply any modifications to damage based on player's cards
                List<PlayerDamageListener> playerPlayerDamageListeners = getPlayerCardEventListeners(playerShooting, PlayerDamageListener.class);
                for (PlayerDamageListener listener : playerPlayerDamageListeners) {
                    damage = listener.onPlayerDamage(damage, null);
                }
                event_PlayerDamage(target, playerShooting, damage, actionQueue);
            }
        } catch (InterruptedException e) {
            // Handle InterruptedException
            System.out.println("Player laser interrupted: " + e.getMessage());
        }
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
     * Method for when a player takes damage.
     * @param playerTakingDamage The player that takes the damage.
     * @param playerInflictingTheDamage If any, the player dealing the damage. If set to null, the source will be interpreted as a board laser.
     * @param damage The damage to deal to the playerTakingDamage.
     * @param actionQueue
     */
    public static void event_PlayerDamage(@NotNull Player playerTakingDamage, Player playerInflictingTheDamage, Damage damage, LinkedList<ActionWithDelay> actionQueue) {
        List<PlayerDamageListener> playerDamageListeners = getPlayerCardEventListeners(playerTakingDamage, PlayerDamageListener.class);
        for (PlayerDamageListener listener : playerDamageListeners) {
            damage = listener.onPlayerDamage(damage, playerTakingDamage);
        }

        // Apply damage to the target player
        boolean anyDamage = false;
        for (DamageTypeAmount damageTypeAmount : damage.getDamageTypeList()) {
            if (damageTypeAmount.getAmount() > 0) {
                anyDamage = true;
                break;
            }
        }
        if (anyDamage) {
            Damage finalDamage = damage;
            actionQueue.addFirst(new ActionWithDelay(() -> {
                finalDamage.applyDamage(playerTakingDamage, playerInflictingTheDamage);
            }, Duration.millis(250)));
        }
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
    public static void event_PlayerPush(Space[][] spaces, Player playerPushing, List<Player> playersToPush, Heading pushDirection, GameController gc) {
        for (Player playerToPush : playersToPush) {
            if (playerToPush == playerPushing) {
                continue;
            }
            if (playerPushing != null) {
                List<PlayerPushListener> playerPushListeners = getPlayerCardEventListeners(playerPushing, PlayerPushListener.class);
                for (PlayerPushListener listener : playerPushListeners) {
                    listener.onPush(playerToPush);
                }
            }
            // Set players new position
            Space nextSpace = playerToPush.getSpace().getSpaceNextTo(pushDirection, spaces);
            if (nextSpace == null || nextSpace.getBoardElement() instanceof BE_Hole) {
                event_PlayerReboot(playerToPush, gc);
            } else {
                playerToPush.setSpace(nextSpace);
            }
        }
    }





    /**
     * Method for when a player moves. This should only be called when a player moves without being pushed.
     */
    public static void event_PlayerMove(Player playerMoving, Space space, GameController gc) {
        List<PlayerMoveListener> playerMoveListeners = getPlayerCardEventListeners(playerMoving, PlayerMoveListener.class);
        boolean shouldReboot = (space == null || space.getBoardElement() instanceof BE_Hole); // If player is out of bounds or on a hole

        // Handle listener logic
        for (PlayerMoveListener listener : playerMoveListeners) {
            Pair<Space, Boolean> movePair = listener.onPlayerMove(space, shouldReboot);
            space = movePair.getKey();
            shouldReboot = movePair.getValue();
        }

        if (shouldReboot) {
            event_PlayerReboot(playerMoving, gc);
        } else {
            playerMoving.setSpace(space);
        }
    }





    /**
     * Method for when a player is rebooted.
     */
    public static void event_PlayerReboot(Player player, GameController gc) {
        System.out.println(player.getName() + " rebooting");
        gc.board.setPhase(Phase.REBOOTING);
        player.startRebooting();
        gc.addPlayerToRebootQueue(player);
        Space oldSpace = player.getSpace();
        if (oldSpace == null) {
            System.out.println("old space null for " + player.getName());
            oldSpace = player.getTemporarySpace();
        }
        Space[][] currentSubBoard = null;
        Space rebootSpace;
        try {
            currentSubBoard = gc.board.getSubBoardOfSpace(oldSpace);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        if (currentSubBoard != null) {
            Pair<Space, BE_Reboot> rebootSpaceFinder = gc.board.findRebootInSubBoard(currentSubBoard);
            if (rebootSpaceFinder != null) {
                rebootSpace = rebootSpaceFinder.getKey();

                // If player reboots on reboot element, check if player push is needed
                Player playerOnRebootSpace = rebootSpace.getPlayer();
                if (playerOnRebootSpace != null && playerOnRebootSpace != player) {
                    List<Player> playersToPush = new ArrayList<>();
                    boolean couldPush = gc.tryMovePlayerInDirection(rebootSpace, rebootSpace.getBoardElement().getDirection(), playersToPush);
                    if (couldPush) {
                        EventHandler.event_PlayerPush(gc.board.getSpaces(), null, playersToPush, rebootSpace.getBoardElement().getDirection(), gc);
                    } else {
                        // There is a wall at the end of player chain
                        System.out.println("ERROR: Can't place player on reboot.");
                        rebootSpace = player.getSpawnPoint();
                    }
                }
            } else {
                rebootSpace = player.getSpawnPoint();
            }
        } else {
            rebootSpace = player.getSpawnPoint();
        }

        player.setTemporarySpace(rebootSpace);
    }
}
