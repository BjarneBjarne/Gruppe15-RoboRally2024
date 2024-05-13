package gruppe15.roborally.model.damage;

import gruppe15.roborally.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Damage {
    private final List<DamageType> damageTypes = new ArrayList<>();

    public Damage() {
        // Initialize instances for each damage type
        damageTypes.add(new Spam(0));
        damageTypes.add(new TrojanHorse(0));
        damageTypes.add(new Worm(0));
        damageTypes.add(new Virus(0));
    }

    public void setAmount(Class<? extends DamageType> damageType, int newAmount) {
        for (DamageType dt : damageTypes) {
            if (dt.getClass().isAssignableFrom(damageType)) {
                dt.setAmount(newAmount);
            }
        }
    }
    public int getAmount(Class<? extends DamageType> damageType) {
        for (DamageType dt : damageTypes) {
            if (dt.getClass().isAssignableFrom(damageType)) {
                return dt.getAmount();
            }
        }
        System.out.println("ERROR: Couldn't find damage type: " + damageType.getName() + " : " + damageType);
        return 0;
    }

    public List<DamageType> getDamageTypes() {
        return damageTypes;
    }

    public void applyDamage(Player player) {
        for (DamageType damageType : damageTypes) {
            damageType.applyDamage(player);
        }
    }
}
