package gruppe15.roborally.model.upgrade_cards;

import gruppe15.roborally.model.upgrade_cards.permanent.*;
import gruppe15.roborally.model.upgrade_cards.temporary.*;

public enum UpgradeCards {
    // Permanent upgrade cards
    ADMIN_PRIVILEGE(Card_AdminPrivilege.class),
    BLUE_SCREEN_OF_DEATH(Card_BlueScreenOfDeath.class),
    BRAKES(Card_Brakes.class),
    CRAB_LEGS(Card_CrabLegs.class),
    DOUBLE_BARREL_LASER(Card_DoubleBarrelLaser.class),
    FIREWALL(Card_Firewall.class),
    HOVER_UNIT(Card_HoverUnit.class),
    PRESSOR_BEAM(Card_PressorBeam.class),
    RAMMING_GEAR(Card_RammingGear.class),
    SCRAMBLER(Card_Scrambler.class),

    // Temporary upgrade cards
    ENERGY_ROUTINE(Card_EnergyRoutine.class),
    HACK(Card_Hack.class),
    MANUAL_SORT(Card_ManualSort.class),
    REBOOT(Card_Reboot.class),
    RECHARGE(Card_Recharge.class),
    RECOMPILE(Card_Recompile.class),
    REPEAT_ROUTINE(Card_RepeatRoutine.class),
    SANDBOX_ROUTINE(Card_SandboxRoutine.class),
    SPAM_BLOCKER(Card_SpamBlocker.class),
    SPAM_FOLDER_ROUTINE(Card_SpamFolderRoutine.class),
    SPEED_ROUTINE(Card_SpeedRoutine.class),
    WEASEL_ROUTINE(Card_WeaselRoutine.class);

    public final Class<? extends UpgradeCard> upgradeCardClass;
    UpgradeCards(Class<? extends UpgradeCard> upgradeCardClass) {
        this.upgradeCardClass = upgradeCardClass;
    }
}
