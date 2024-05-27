package gruppe15.roborally.model.upgrade_cards.permanent;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.EventHandler;
import gruppe15.roborally.model.Laser;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.events.PlayerShootListener;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardPermanent;

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
                System.out.println("Player: \"" + owner.getName() + "\" used UpgradeCard: \"" + title + "\".");
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
