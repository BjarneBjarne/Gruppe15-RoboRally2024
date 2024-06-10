package com.gruppe15.model.upgrade_cards.permanent;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.EventHandler;
import com.gruppe15.model.Player;
import com.gruppe15.model.damage.DamageType;
import com.gruppe15.model.events.PlayerPushListener;
import com.gruppe15.model.upgrade_cards.UpgradeCardPermanent;

public class Card_VirusModule extends UpgradeCardPermanent {

    public Card_VirusModule() {
        super("Virus Module", 2);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        eventListeners.add(EventHandler.subscribe((PlayerPushListener) (playerPushing, playerToPush, damage) -> {
            if (owner == playerPushing) {
                printUsage();
                damage.addAmount(DamageType.VIRUS, 1);
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
