package com.gruppe15.model.upgrade_cards.permanent;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.EventHandler;
import com.gruppe15.model.Player;
import com.gruppe15.model.damage.DamageType;
import com.gruppe15.model.events.PlayerLaserHitListener;
import com.gruppe15.model.events.PlayerPushListener;
import com.gruppe15.model.upgrade_cards.UpgradeCardPermanent;

public class Card_TrojanNeedler extends UpgradeCardPermanent {

    public Card_TrojanNeedler() {
        super("Trojan Needler", 3, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerLaserHitListener) (damage, playerTakingDamage) -> {
            if (owner != playerTakingDamage) {
                printUsage();
                if (damage.getAmount(DamageType.SPAM) > 0) {
                    // Modifying damage
                    damage.subtractAmount(DamageType.SPAM, 1);
                    damage.addAmount(DamageType.TROJAN_HORSE, 1);
                }
            }
            return damage;
        }, owner));

        // On push
        eventListeners.add(EventHandler.subscribe((PlayerPushListener) (playerPushing, playerToPush, damage) -> {
            if (owner == playerPushing) {
                if (damage.getAmount(DamageType.SPAM) > 0) {
                    printUsage();
                    // Modifying damage
                    damage.subtractAmount(DamageType.SPAM, 1);
                    damage.addAmount(DamageType.TROJAN_HORSE, 1);
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
