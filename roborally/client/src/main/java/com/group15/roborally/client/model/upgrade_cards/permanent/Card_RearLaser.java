package com.group15.roborally.client.model.upgrade_cards.permanent;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.EventHandler;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.events.PlayerShootListener;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardPermanent;

import java.util.List;

import static com.group15.roborally.client.model.EventHandler.getPlayerCardEventListeners;

public class Card_RearLaser extends UpgradeCardPermanent {

    public Card_RearLaser() {
        super("Rear Laser", 2, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events
        eventListeners.add(EventHandler.subscribe((PlayerShootListener) (playerShooting, laser,rear) -> {
            if(!rear) {
                if (playerShooting == owner) {
                    printUsage();
                    // Adding another laser
                    owner.shootLaser(owner.getHeading().opposite());


                }
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