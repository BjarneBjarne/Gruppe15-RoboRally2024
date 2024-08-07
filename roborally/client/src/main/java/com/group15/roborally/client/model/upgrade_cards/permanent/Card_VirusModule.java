package com.group15.roborally.client.model.upgrade_cards.permanent;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.EventHandler;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.damage.DamageType;
import com.group15.roborally.client.model.events.PlayerPushListener;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardPermanent;

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
    public void onActivated() {

    }
}
