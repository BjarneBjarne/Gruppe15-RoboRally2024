package com.gruppe15.model.upgrade_cards.permanent;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.Player;
import com.gruppe15.model.EventHandler;
import com.gruppe15.model.damage.DamageType;
import com.gruppe15.model.upgrade_cards.UpgradeCardPermanent;
import com.gruppe15.model.events.PlayerLaserHitListener;

public class Card_DoubleBarrelLaser extends UpgradeCardPermanent {

    public Card_DoubleBarrelLaser() {
        super("Double Barrel Laser", 2, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerLaserHitListener) (damage, playerTakingDamage) -> {
            if (owner != playerTakingDamage) {
                printUsage();
                // Modifying damage
                damage.addAmount(DamageType.SPAM, 1);
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
