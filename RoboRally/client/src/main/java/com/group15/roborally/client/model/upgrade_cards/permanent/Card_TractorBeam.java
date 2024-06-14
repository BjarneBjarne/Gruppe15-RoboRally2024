package com.group15.model.upgrade_cards.permanent;

import com.group15.roborally.controller.GameController;
import com.group15.model.*;
import com.group15.model.events.PlayerLaserHitListener;
import com.group15.model.upgrade_cards.UpgradeCardPermanent;
import com.group15.roborally.server.model.Player;

public class Card_TractorBeam extends UpgradeCardPermanent {

    public Card_TractorBeam() {
        super("Tractor Beam", 3, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerLaserHitListener) (damage, playerTakingDamage) -> {
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

                Space spaceToPullTo = targetSpace.getSpaceNextTo(pullDirection, owner.board.getSpaces());
                if (!targetSpace.getIsWallBetween(spaceToPullTo)) {
                    owner.board.getBoardActionQueue().add(new ActionWithDelay(() -> {
                        playerTakingDamage.setSpace(spaceToPullTo);
                    }, 0, "Pulling with Tractor Beam"));
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
