package gruppe15.roborally.model.upgrades;

import gruppe15.roborally.model.Command;
import gruppe15.roborally.model.upgrades.upgrade_cards.Card_Brakes;
import gruppe15.roborally.model.upgrades.upgrade_cards.Card_DoubleBarrelLaser;
import gruppe15.roborally.model.upgrades.upgrade_cards.Card_HoverUnit;

import java.util.Arrays;
import java.util.Collections;

public enum UpgradeCards {
    BRAKES(Card_Brakes.class),
    DOUBLE_BARREL_LASER(Card_DoubleBarrelLaser.class),
    HOVER_UNIT(Card_HoverUnit.class);
    public final Class<? extends UpgradeCard> upgradeCardClass;
    UpgradeCards(Class<? extends UpgradeCard> upgradeCardClass) {
        this.upgradeCardClass = upgradeCardClass;
    }
}
