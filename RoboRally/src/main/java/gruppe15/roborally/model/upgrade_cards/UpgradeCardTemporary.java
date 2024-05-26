package gruppe15.roborally.model.upgrade_cards;

import gruppe15.roborally.model.CardField;
import gruppe15.roborally.model.Phase;

public abstract class UpgradeCardTemporary extends UpgradeCard {

    public UpgradeCardTemporary(String title, int purchaseCost, int useCost, int maxUses, Phase refreshedOn, Phase... activatableOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn, activatableOn);
    }

    public UpgradeCardTemporary(String title, int purchaseCost, int useCost, int maxUses, Phase refreshedOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn);
    }

    @Override
    public void tryActivate() {
        if (canBeActivated()) {
            this.currentUses--;
            this.onActivated();

            // Remove card on use
            if (refreshedOn == null && this.currentUses == 0) {
                unInitialize();
            }
        }
    }

    @Override
    protected void onActivated() {
        System.out.println("Player: \"" + owner.getName() + "\" used UpgradeCard: \"" + title + "\".");
        if (this.currentUses == 0) {
            for (CardField cardField : owner.getTemporaryUpgradeCardFields()) {
                if (cardField.getCard() == this) {
                    cardField.setCard(null);
                }
            }
        }
    }
}
