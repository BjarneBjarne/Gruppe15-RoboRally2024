package com.gruppe15.model.upgrade_cards.permanent;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.EventHandler;
import com.gruppe15.model.Player;
import com.gruppe15.model.damage.DamageType;
import com.gruppe15.model.events.PlayerLaserHitListener;
import com.gruppe15.model.upgrade_cards.UpgradeCardPermanent;

public class Card_Firewall extends UpgradeCardPermanent {

    public Card_Firewall() {
        super("Firewall", 3, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerLaserHitListener) (damage, playerTakingDamage) -> {
            if (owner == playerTakingDamage && owner.getIsRebooting()) {
                printUsage();
                // Modifying damage
                damage.setAmount(DamageType.SPAM, 0);
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
