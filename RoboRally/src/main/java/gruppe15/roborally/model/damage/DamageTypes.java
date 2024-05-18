package gruppe15.roborally.model.damage;

import gruppe15.roborally.model.Command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DamageTypes {
    SPAM("Spam", Command.SPAM, Spam.class),
    TROJAN_HORSE("TrojanHorse", Command.TROJAN_HORSE, TrojanHorse.class),
    WORM("Worm", Command.WORM, Worm.class),
    VIRUS("Virus", Command.VIRUS, Virus.class);

    final public String displayName;
    private final Command commandCardType;
    private final Class<? extends DamageType> damageType;

    DamageTypes(String displayName, Command commandCardType, Class<? extends DamageType> damageType) {
        this.displayName = displayName;
        this.commandCardType = commandCardType;
        this.damageType = damageType;
    }
    public Class<? extends DamageType> getDamageType() {
        return damageType;
    }
    public Command getCommandCardType() {
        return commandCardType;
    }
}
