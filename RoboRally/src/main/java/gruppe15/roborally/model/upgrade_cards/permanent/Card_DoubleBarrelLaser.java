package gruppe15.roborally.model.upgrade_cards.permanent;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.EventHandler;
import gruppe15.roborally.model.damage.DamageType;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardPermanent;
import gruppe15.roborally.model.events.PlayerLaserHitListener;

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
