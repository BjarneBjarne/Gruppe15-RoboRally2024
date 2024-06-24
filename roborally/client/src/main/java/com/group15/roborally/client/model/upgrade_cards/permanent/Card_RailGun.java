package com.group15.roborally.client.model.upgrade_cards.permanent;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.EventHandler;
import com.group15.roborally.client.model.Laser;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.events.PlayerShootListener;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardPermanent;

public class Card_RailGun extends UpgradeCardPermanent {

    public Card_RailGun() {
        super("Rail Gun", 2, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        eventListeners.add(EventHandler.subscribe((PlayerShootListener) (playerShooting, laser,rear) -> {
            if (playerShooting == owner) {
                printUsage();
                // Modifying laser

                laser = new Laser(laser.origin, laser.direction, laser.owner); // No collision
            }
            return laser;
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
