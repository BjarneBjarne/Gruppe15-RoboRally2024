package com.gruppe15.model.upgrade_cards;

import com.gruppe15.model.CardField;
import com.gruppe15.model.Phase;

public abstract class UpgradeCardTemporary extends UpgradeCard {

    public UpgradeCardTemporary(String title, int purchaseCost, int useCost, int maxUses, Phase refreshedOn, Phase... activatableOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn, activatableOn);
    }

    public UpgradeCardTemporary(String title, int purchaseCost, int useCost, int maxUses, Phase refreshedOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn);
    }

    @Override
    protected void onActivated() {
        printUsage();
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
