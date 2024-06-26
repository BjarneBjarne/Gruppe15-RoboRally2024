package com.group15.roborally.client.model.upgrade_cards;

import com.group15.roborally.client.model.CardField;
import com.group15.roborally.server.model.GamePhase;

public abstract class UpgradeCardTemporary extends UpgradeCard {

    public UpgradeCardTemporary(String title, int purchaseCost, int useCost, int maxUses, GamePhase refreshedOn, GamePhase... activatableOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn, activatableOn);
    }

    public UpgradeCardTemporary(String title, int purchaseCost, int useCost, int maxUses, GamePhase refreshedOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn);
    }

    @Override
    public void tryActivate() {
        super.tryActivate();
        // Remove card on use
        if (refreshedOn == null && this.currentUses <= 0) {
            unInitialize();
        }
    }

    @Override
    public void unInitialize() {
        // Remove from player UI
        for (CardField cardField : owner.getTemporaryUpgradeCardFields()) {
            if (cardField.getCard() == this) {
                cardField.setCard(null);
            }
        }
        super.unInitialize();
    }
}
