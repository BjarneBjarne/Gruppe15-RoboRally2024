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

import static gruppe15.roborally.GameSettings.STANDARD_DAMAGE;

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
     * Method for letting a player laser start at the EventManager, letting PlayerShootListeners modify the "original" laser.
     * This should ONLY be called from within player.queueLaser().
     * @param playerShooting The player to begin shooting a laser.
     */
    public static void event_PlayerShootStart(Player playerShooting) {
        if (playerShooting.getIsRebooting()) {
            return;
        }

        // Making new laser
        Laser laser = new Laser(playerShooting.getSpace(), playerShooting.getHeading(), playerShooting, Player.class, Space.class);
        // Modify laser
        List<PlayerShootListener> playerShootListeners = getPlayerCardEventListeners(playerShooting, PlayerShootListener.class);
        for (PlayerShootListener listener : playerShootListeners) {
            laser = listener.onPlayerShoot(playerShooting, laser);
        }

        event_PlayerShootHandle(playerShooting, laser);
    }

    /**
     * Method for whenever a player has begun to shoot. Mainly calculates and distributes damage.
     * @param playerShooting The player who is currently shooting.
     * @param laser The laser that playerShooting fired.
     */
    public static void event_PlayerShootHandle(Player playerShooting, Laser laser) {
        if (laser == null) return;

        Board board = playerShooting.board;

        // Start the laser
        laser.startLaser(board.getSpaces()).run();

        try {
            List<Player> playersHit = calculatePlayersHit(laser);
            // Deal damage to each target player
            for (Player target : playersHit) {
                if (target == playerShooting) {
                    continue;
                }
                Damage damage = new Damage(0, 0, 0, 0);
                damage.add(STANDARD_DAMAGE);
                damage.add(playerShooting.getPermanentBonusDamage());
                damage.add(playerShooting.useTemporaryBonusDamage());

                // Apply any modifications to damage based on player's cards
                List<PlayerLaserHitListener> playerPlayerLaserHitListeners = getPlayerCardEventListeners(playerShooting, PlayerLaserHitListener.class);
                for (PlayerLaserHitListener listener : playerPlayerLaserHitListeners) {
                    damage = listener.onPlayerDamage(damage, target);
                }
                event_PlayerDamage(target, playerShooting, damage);
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
    public static void event_PlayerDamage(@NotNull Player playerTakingDamage, Player playerInflictingTheDamage, Damage damage) {
        LinkedList<ActionWithDelay> actionQueue = playerInflictingTheDamage.board.getBoardActionQueue();
        List<PlayerLaserHitListener> playerLaserHitListeners = getPlayerCardEventListeners(playerTakingDamage, PlayerLaserHitListener.class);
        for (PlayerLaserHitListener listener : playerLaserHitListeners) {
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
    private static final Map<Player, Player> pushPairs = new HashMap<>();
    public static void event_PlayerPush(Space[][] spaces, Player playerPushing, List<Player> playersToPush, Heading pushDirection, GameController gc) {
        for (Player playerToPush : playersToPush) {
            if (playerToPush == playerPushing) continue; // Player can't push themselves.
            if (!playerToPush.equals(pushPairs.get(playerPushing)) && !playerPushing.getIsRebooting()) {
                // playerToPush haven't been pushed this movement.
                // Normally there is no damage on push.
                Damage damage = new Damage(0, 0, 0, 0);

                // Players being pushed modifiers
                List<PlayerPushListener> playerPushedListeners = getPlayerCardEventListeners(playerToPush, PlayerPushListener.class);
                for (PlayerPushListener listener : playerPushedListeners) {
                    listener.onPush(playerPushing, playerToPush, damage);
                }

                // Player pushing modifiers
                if (playerPushing != null) {
                    List<PlayerPushListener> playerPushingListeners = getPlayerCardEventListeners(playerPushing, PlayerPushListener.class);
                    for (PlayerPushListener listener : playerPushingListeners) {
                        listener.onPush(playerPushing, playerToPush, damage);
                    }
                }

                event_PlayerDamage(playerToPush, playerPushing, damage);
            }

            // Set players new position
            Space nextSpace = playerToPush.getSpace().getSpaceNextTo(pushDirection, spaces);
            playerToPush.setSpace(nextSpace);
            nextSpace.updateSpace();

            // If the player is still moving, we remember that they pushed playerToPush.
            if (Math.abs(playerPushing.getVelocity().forward) + Math.abs(playerPushing.getVelocity().right) > 0) {
                pushPairs.put(playerPushing, playerToPush);
            } else {
                // When the player is standing still, we forget everyone they pushed.
                pushPairs.remove(playerPushing);
            }
        }
    }





    /**
     * Method for when a player moves. This should only be called when a player moves without being pushed.
     */
    public static void event_PlayerMove(Player playerMoving, Space space, GameController gc) {
        playerMoving.setSpace(space);
        /*List<PlayerEndOfActionListener> playerMoveListeners = getPlayerCardEventListeners(playerMoving, PlayerEndOfActionListener.class);
        boolean shouldReboot = (space == null || space.getBoardElement() instanceof BE_Hole); // If player is out of bounds or on a hole

        // Handle listener logic
        for (PlayerEndOfActionListener listener : playerMoveListeners) {
            Pair<Space, Boolean> movePair = listener.onPlayerMove(space, shouldReboot);
            space = movePair.getKey();
            shouldReboot = movePair.getValue();
        }

        if (shouldReboot) {
            event_PlayerReboot(playerMoving, true, gc);
        } else {
            playerMoving.setSpace(space);
        }*/
    }





    /**
     * Method for when a player is rebooted.
     */
    public static void event_PlayerReboot(Player player, boolean takeDamage, GameController gc) {
        Space oldSpace = player.getSpace();
        if (oldSpace == null) {
            //System.out.println("old space null for " + player.getName());
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
            if (rebootSpaceFinder == null) {
                // If no reboot token on current subboard, look for reboot token on start subboard
                rebootSpaceFinder = gc.board.findRebootInSubBoard(gc.board.getSubBoardOfSpace(player.getSpawnPoint()));
            }
            if (rebootSpaceFinder != null) {
                rebootSpace = rebootSpaceFinder.getKey();

                // If player reboots on reboot element, check if player push is needed
                Player playerOnRebootSpace = rebootSpace.getPlayer();
                if (playerOnRebootSpace != null && playerOnRebootSpace != player) {
                    List<Player> playersToPush = new ArrayList<>();
                    boolean couldPush = gc.board.tryMovePlayerInDirection(rebootSpace, rebootSpace.getBoardElement().getDirection(), playersToPush);
                    if (couldPush) {
                        EventHandler.event_PlayerPush(gc.board.getSpaces(), player, playersToPush, rebootSpace.getBoardElement().getDirection(), gc);
                    } else {
                        // There is a wall at the end of player chain
                        System.out.println("ERROR: Can't place player on reboot.");
                        rebootSpace = player.getSpawnPoint();
                    }
                }
            } else {
                // If no reboot token on start subboard, start on player spawn
                rebootSpace = player.getSpawnPoint();
            }
        } else {
            rebootSpace = player.getSpawnPoint();
        }

        Player otherPlayerOnSpawnpoint = null;
        if (rebootSpace == player.getSpawnPoint()) {
            if (rebootSpace.getPlayer() != null && rebootSpace.getPlayer() != player) {
                otherPlayerOnSpawnpoint = rebootSpace.getPlayer();
            }
        }
        player.setSpace(rebootSpace);
        player.startRebooting(gc, takeDamage);

        if (otherPlayerOnSpawnpoint != null) {
            event_PlayerReboot(otherPlayerOnSpawnpoint, true, gc);
        }
    }

    public static void event_EndOfAction(GameController gameController) {
        for (Player player : gameController.board.getPlayers()) {
            // Handle if a player should reboot
            Space playerSpace = player.getSpace();
            List<PlayerEndOfActionListener> playerEndOfActionListeners = getPlayerCardEventListeners(player, PlayerEndOfActionListener.class);
            boolean shouldReboot = (playerSpace == null || playerSpace.getBoardElement() instanceof BE_Hole); // If player is out of bounds or on a hole
            // Handle listener logic
            for (PlayerEndOfActionListener listener : playerEndOfActionListeners) {
                shouldReboot = listener.onEndOfAction(playerSpace, shouldReboot);
            }
            if (shouldReboot) {
                event_PlayerReboot(player, true, gameController);
            }
        }
    }
}
