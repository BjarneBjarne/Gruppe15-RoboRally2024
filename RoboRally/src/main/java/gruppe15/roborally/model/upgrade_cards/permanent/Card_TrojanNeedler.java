package gruppe15.roborally.model.upgrade_cards.permanent;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.EventHandler;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.damage.DamageType;
import gruppe15.roborally.model.events.PlayerLaserHitListener;
import gruppe15.roborally.model.events.PlayerPushListener;
import gruppe15.roborally.model.upgrade_cards.UpgradeCardPermanent;

public class Card_TrojanNeedler extends UpgradeCardPermanent {

    public Card_TrojanNeedler() {
        super("Trojan Needler", 3, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerLaserHitListener) (damage, playerTakingDamage) -> {
            if (owner != playerTakingDamage) {
                printUsage();
                if (damage.getAmount(DamageType.SPAM) > 0) {
                    // Modifying damage
                    damage.subtractAmount(DamageType.SPAM, 1);
                    damage.addAmount(DamageType.TROJAN_HORSE, 1);
                }
            }
            return damage;
        }, owner));

        // On push
        eventListeners.add(EventHandler.subscribe((PlayerPushListener) (playerPushing, playerToPush, damage) -> {
            if (owner == playerPushing) {
                if (damage.getAmount(DamageType.SPAM) > 0) {
                    printUsage();
                    // Modifying damage
                    damage.subtractAmount(DamageType.SPAM, 1);
                    damage.addAmount(DamageType.TROJAN_HORSE, 1);
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
