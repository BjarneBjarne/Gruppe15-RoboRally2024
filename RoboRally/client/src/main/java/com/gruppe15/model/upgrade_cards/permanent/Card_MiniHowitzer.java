package com.gruppe15.model.upgrade_cards.permanent;

import com.gruppe15.ApplicationSettings;
import com.gruppe15.controller.GameController;
import com.gruppe15.model.*;
import com.gruppe15.model.damage.Damage;
import com.gruppe15.model.events.PlayerLaserHitListener;
import com.gruppe15.model.upgrade_cards.UpgradeCardPermanent;

import java.util.ArrayList;
import java.util.List;

public class Card_MiniHowitzer extends UpgradeCardPermanent {
    private boolean activated = true;

    public Card_MiniHowitzer() {
        super("Mini Howitzer", 2, 1, 1, Phase.PLAYER_ACTIVATION, Phase.BOARD_ACTIVATION);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerLaserHitListener) (damage, playerTakingDamage) -> {
            if (owner != playerTakingDamage) {
                if (activated) {
                    activated = false;
                    if (ApplicationSettings.DEBUG_SHOW_UPGRADE_CARD_USAGE) {
                        System.out.println("Player: \"" + owner.getName() + "\" used UpgradeCard: \"" + title + "\" to push and deal two more SPAM damage to player: \"" + playerTakingDamage + "\".");
                    }
                    // pushDirection calculated for compatibility with e.g. "REAR LASER" UpgradeCardPermanent.
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

                    EventHandler.event_PlayerPush(owner.board.getSpaces(), owner, playerToPush, pushDirection);
                    // Extra damage already added as owner temporaryBonusDamage
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
        super.onActivated();
        owner.addTemporaryBonusDamage(new Damage(2, 0, 0, 0));
        activated = true;
    }
}
