package gruppe15.roborally.model.upgrades.upgradeCards;

import gruppe15.roborally.model.upgrades.UpgradeCardsHandler;

public class Card_BlueScreenOfDeath {

    public Card_BlueScreenOfDeath() {

    }

    public void onAddedToPlayer(UpgradeCardsHandler upgradeCardsHandler) {
        upgradeCardsHandler.setOnDamageDealt((player, targetList) -> {

        });
    }
}
