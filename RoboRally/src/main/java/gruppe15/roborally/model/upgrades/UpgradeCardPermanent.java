package gruppe15.roborally.model.upgrades;

abstract class UpgradeCardPermanent extends UpgradeCard {
    @Override
    public void activate() {
        // Permanent upgrades can't necessarily be activated. This is the default behavior for those.

    }
}
