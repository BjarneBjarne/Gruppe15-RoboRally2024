package com.group15.roborally.client.model.upgrade_cards.permanent;

import com.group15.roborally.client.ApplicationSettings;
import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.events.PlayerLaserHitListener;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardPermanent;
import com.group15.roborally.common.model.GamePhase;

public class Card_DeflectorShield extends UpgradeCardPermanent {
    private boolean activated = false;

    public Card_DeflectorShield() {
        super("Deflector Shield", 2, 1, 1, GamePhase.PLAYER_ACTIVATION, false, GamePhase.PLAYER_ACTIVATION);
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
    public void onActivated() {
        
        activated = true;
    }
}
