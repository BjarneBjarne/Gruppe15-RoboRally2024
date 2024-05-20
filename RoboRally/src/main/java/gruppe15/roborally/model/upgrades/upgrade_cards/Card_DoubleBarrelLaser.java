package gruppe15.roborally.model.upgrades.upgrade_cards;

import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.EventHandler;
import gruppe15.roborally.model.damage.DamageTypes;
import gruppe15.roborally.model.upgrades.UpgradeCardPermanent;
import gruppe15.roborally.model.events.PlayerDamageListener;

public class Card_DoubleBarrelLaser extends UpgradeCardPermanent {

    public Card_DoubleBarrelLaser() {
        super("Double Barrel Laser", 2, 0, 0, null);
    }

    @Override
    public void initialize(Board board, Player owner) {
        super.initialize(board, owner);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerDamageListener) (damage, playerTakingDamage) -> {
            if (owner != playerTakingDamage) {
                //System.out.println("Player {" + owner.getName() + "} has a Double Barrel Laser!");

                // Modifying damage
                damage.setAmount(DamageTypes.SPAM, damage.getAmount(DamageTypes.SPAM) + 1);
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
