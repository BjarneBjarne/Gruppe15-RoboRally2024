package gruppe15.roborally.model.upgrades.upgradeInterfaces;

import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.upgrades.EffectTrigger;

import java.util.List;

@FunctionalInterface
public interface DamageDealtListener extends EffectTrigger {
    void onDamageDealt(Player owner, List<Player> targets);
}
