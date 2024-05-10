package gruppe15.roborally.model.upgrades.upgrade_cards;

import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Phase;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.EventHandler;
import gruppe15.roborally.model.damage.Spam;
import gruppe15.roborally.model.upgrades.UpgradeCardPermanent;
import gruppe15.roborally.model.events.PlayerDamageListener;

public class Card_DoubleBarrelLaser extends UpgradeCardPermanent {

    public Card_DoubleBarrelLaser(String title, int purchaseCost, int useCost, int maxUses, Phase refreshedOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn);
    }

    @Override
    public void initialize(Board board, Player owner) {
        super.initialize(board, owner);

        // Defining effects on events

        // OnDamageDealt
        EventHandler.onEvent((PlayerDamageListener) damage -> {
            System.out.println("Board is at register " + board.getCurrentRegister() + ".");
            System.out.println("{" + owner.getName() + "} has a Double Barrel Laser!");

            // Modifying damage
            damage.setAmount(Spam.class, damage.getAmount(Spam.class) + 1);

            return damage;
        }, owner);
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
