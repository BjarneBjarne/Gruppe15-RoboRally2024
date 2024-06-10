package com.gruppe15.model.upgrade_cards.permanent;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.EventHandler;
import com.gruppe15.model.Laser;
import com.gruppe15.model.Player;
import com.gruppe15.model.events.PlayerShootListener;
import com.gruppe15.model.upgrade_cards.UpgradeCardPermanent;

public class Card_RailGun extends UpgradeCardPermanent {

    public Card_RailGun() {
        super("Rail Gun", 2, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        eventListeners.add(EventHandler.subscribe((PlayerShootListener) (playerShooting, laser) -> {
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
    protected void onActivated() {

    }
}
