package gruppe15.roborally.model.upgrades.upgrade_cards;

import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.EventHandler;
import gruppe15.roborally.model.upgrades.UpgradeCardPermanent;

public class Card_BlueScreenOfDeath extends UpgradeCardPermanent {

    @Override
    public void initialize(Player owner) {
        // Defining effects on events
        EventHandler.setOnDamageDealt((player, targetList) -> {
            if (player == owner) {
                StringBuilder damageMsg = new StringBuilder("{" + player.getName() + "} deals damage to [");
                for (int i = 0; i < targetList.size(); i++) {
                    if (i > 0) {
                        damageMsg.append(", ");
                    }
                    Player target = targetList.get(i);
                    damageMsg.append(target.getName());
                }
                damageMsg.append("]");
                System.out.println(damageMsg);
            }
        });
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
