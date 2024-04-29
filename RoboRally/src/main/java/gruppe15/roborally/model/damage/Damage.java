package gruppe15.roborally.model.damage;

import gruppe15.roborally.model.Player;

import java.util.HashMap;
import java.util.Map;

public class Damage {
    private Map<Class<? extends DamageType>, DamageType> damageMap;

    public Damage() {
        damageMap = new HashMap<>();
        // Initialize instances for each damage type
        damageMap.put(Spam.class, new Spam(0));
        damageMap.put(TrojanHorse.class, new TrojanHorse(0));
        damageMap.put(Worm.class, new Worm(0));
        damageMap.put(Virus.class, new Virus(0));
    }

    public void setAmount(Class<? extends DamageType> damageType, int newAmount) {
        DamageType damage = damageMap.get(damageType);
        if (damage != null) {
            damage.setAmount(newAmount);
        }
    }
    public int getAmount(Class<? extends DamageType> damageType) {
        DamageType damage = damageMap.get(damageType);
        return damage.getAmount();
    }

    public Map<Class<? extends DamageType>, DamageType> getDamageMap() {
        return damageMap;
    }

    public void applyDamage(Player player) {
        for (DamageType damageType : damageMap.values()) {
            damageType.applyDamage(player);
        }
    }
}
