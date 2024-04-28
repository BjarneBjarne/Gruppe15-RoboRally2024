package gruppe15.roborally.model.upgrades;

import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.upgrades.upgradeInterfaces.DamageDealtListener;

import java.util.ArrayList;
import java.util.List;

public class UpgradeCardsHandler {
    private static final List<DamageDealtListener> DDListeners = new ArrayList<>();

    public static void setOnDamageDealt(DamageDealtListener DDListener) {
        DDListeners.add(DDListener);
    }
    public static void dealDamage(Player owner, List<Player> targetList) {
        if (!DDListeners.isEmpty())
            for (DamageDealtListener listener : DDListeners) {
                listener.onDamageDealt(owner, targetList);
            }
    }
}
