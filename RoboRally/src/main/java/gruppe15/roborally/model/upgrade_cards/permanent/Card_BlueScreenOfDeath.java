package gruppe15.roborally.model.upgrade_cards.permanent;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.EventHandler;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.damage.DamageTypes;
import gruppe15.roborally.model.events.PlayerDamageListener;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardPermanent;

public class Card_BlueScreenOfDeath extends UpgradeCardPermanent {

    public Card_BlueScreenOfDeath() {
        super("Blue Screen of Death", 4, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerDamageListener) (damage, playerTakingDamage) -> {
            if (owner != playerTakingDamage) {
                if (damage.getAmount(DamageTypes.SPAM) > 0) {
                    System.out.println("Player: \"" + owner.getName() + "\" used UpgradeCard: \"" + title + "\".");
                    // Modifying damage
                    damage.setAmount(DamageTypes.SPAM, damage.getAmount(DamageTypes.SPAM) - 1);
                    damage.setAmount(DamageTypes.WORM, damage.getAmount(DamageTypes.WORM) + 1);
                }
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
