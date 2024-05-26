package gruppe15.roborally.model.upgrade_cards.permanent;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.EventHandler;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.damage.DamageTypes;
import gruppe15.roborally.model.events.PlayerDamageListener;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardPermanent;

import java.util.ArrayList;
import java.util.List;

public class Card_PressorBeam extends UpgradeCardPermanent {

    public Card_PressorBeam() {
        super("Pressor Beam", 3, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerDamageListener) (damage, playerTakingDamage) -> {
            if (owner != playerTakingDamage) {
                List<Player> playerToPush = new ArrayList<>();
                playerToPush.add(playerTakingDamage);
                Heading pushDirection;
                Space ownerSpace = owner.getSpace();
                Space targetSpace = playerTakingDamage.getSpace();
                if (ownerSpace.y > targetSpace.y) {
                    pushDirection = Heading.NORTH;
                } else if (ownerSpace.y < targetSpace.y) {
                    pushDirection = Heading.SOUTH;
                } else if (ownerSpace.x < targetSpace.x) {
                    pushDirection = Heading.EAST;
                } else {
                    pushDirection = Heading.WEST;
                }
                // pushDirection calculated for compatibility with e.g. "REAR LASER" UpgradeCardPermanent.
                EventHandler.event_PlayerPush(owner.board.getSpaces(), owner, playerToPush, pushDirection, gameController);
                if (damage.getAmount(DamageTypes.SPAM) > 0) {
                    System.out.println("Player: \"" + owner.getName() + "\" used UpgradeCard: \"" + title + "\".");
                    // Modifying damage
                    damage.setAmount(DamageTypes.SPAM, damage.getAmount(DamageTypes.SPAM) - 1);
                    damage.setAmount(DamageTypes.WORM, damage.getAmount(DamageTypes.WORM) + 1);
                }
            }
            return damage;
        }, owner));
    }

    @Override
    protected void onEnabled() {

    }

    @Override
    protected void onDisabled() {

    }

    @Override
    protected void onActivated() {

    }
}
