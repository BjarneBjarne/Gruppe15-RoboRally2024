package gruppe15.roborally.model.upgrades;

import gruppe15.roborally.model.Phase;

public abstract class UpgradeCardTemporary extends UpgradeCard {

    public UpgradeCardTemporary(String title, int purchaseCost, int useCost, int maxUses, Phase refreshedOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn);
    }
}
