package gruppe15.roborally.model.damage;

import gruppe15.roborally.model.Command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DamageTypes {
    SPAM("Spam", Spam.class),
    TROJAN_HORSE("TrojanHorse", TrojanHorse.class),
    VIRUS("Virus", Virus.class),
    WORM("Worm", Worm.class);

    final public String displayName;
    private Class<? extends DamageType> damageType;

    DamageTypes(String displayName, Class<? extends DamageType> damageType) {
        this.displayName = displayName;
        this.damageType = damageType;
    }
    public Class<? extends DamageType> getDamageType() {
        return damageType;
    }
}
