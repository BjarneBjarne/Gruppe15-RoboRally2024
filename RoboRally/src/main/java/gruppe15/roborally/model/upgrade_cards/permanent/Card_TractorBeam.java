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

public class Card_TractorBeam extends UpgradeCardPermanent {

    public Card_TractorBeam() {
        super("Tractor Beam", 3, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerDamageListener) (damage, playerTakingDamage) -> {
            if (owner != playerTakingDamage) {
                Space ownerSpace = owner.getSpace();
                Space targetSpace = playerTakingDamage.getSpace();

                if (targetSpace.getDistanceFromOtherSpace(ownerSpace) <= 1) {
                    return damage;
                }

                // pullDirection calculated for compatibility with e.g. "REAR LASER" UpgradeCardPermanent.
                Heading pullDirection;
                if (ownerSpace.y > targetSpace.y) {
                    pullDirection = Heading.SOUTH;
                } else if (ownerSpace.y < targetSpace.y) {
                    pullDirection = Heading.NORTH;
                } else if (ownerSpace.x < targetSpace.x) {
                    pullDirection = Heading.WEST;
                } else {
                    pullDirection = Heading.EAST;
                }

                if (!targetSpace.getIsWallBetween(targetSpace.getSpaceNextTo(pullDirection, owner.board.getSpaces()))) {
                    // TODO: Queue action with pull
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
