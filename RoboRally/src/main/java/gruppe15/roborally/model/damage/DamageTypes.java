package gruppe15.roborally.model.damage;

import gruppe15.roborally.model.Command;

public enum DamageTypes {
    SPAM("Spam"),
    TROJAN_HORSE("Trojan Horse"),
    WORM("Worm"),
    VIRUS("Virus");

    public final String displayName;
    private Command commandCardType;

    DamageTypes(String displayName) {
        this.displayName = displayName;
    }
    public void setCommandCardType(Command commandCardType) {
        this.commandCardType = commandCardType;
    }
    public Command getCommandCardType() {
        return commandCardType;
    }
}
