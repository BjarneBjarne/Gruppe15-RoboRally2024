package com.group15.model.upgrade_cards.permanent;

import com.group15.ApplicationSettings;
import com.group15.controller.GameController;
import com.group15.model.*;
import com.group15.model.events.PlayerLaserHitListener;
import com.group15.model.upgrade_cards.UpgradeCardPermanent;

public class Card_DeflectorShield extends UpgradeCardPermanent {
    private boolean activated = true;

    public Card_DeflectorShield() {
        super("Deflector Shield", 2, 1, 1, Phase.PLAYER_ACTIVATION, Phase.PLAYER_ACTIVATION);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerLaserHitListener) (damage, playerTakingDamage) -> {
            if (owner == playerTakingDamage) {
                if (activated) {
                    activated = false;
                    if (ApplicationSettings.DEBUG_SHOW_UPGRADE_CARD_USAGE) {
                        System.out.println("Player: \"" + owner.getName() + "\" used UpgradeCard: \"" + title + "\" to negate laser damage.");
                    }
                    damage.clear();
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
        activated = true;
    }
}