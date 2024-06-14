package com.group15.model.upgrade_cards.permanent;

import com.group15.controller.GameController;
import com.group15.model.EventHandler;
import com.group15.model.Player;
import com.group15.model.damage.DamageType;
import com.group15.model.events.PlayerPushListener;
import com.group15.model.upgrade_cards.UpgradeCardPermanent;

public class Card_RammingGear extends UpgradeCardPermanent {

    public Card_RammingGear() {
        super("Ramming Gear", 2, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        eventListeners.add(EventHandler.subscribe((PlayerPushListener) (playerPushing, playerToPush, damage) -> {
            if (owner == playerPushing) {
                printUsage();
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
