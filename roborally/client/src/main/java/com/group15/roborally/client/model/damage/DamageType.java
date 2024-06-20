package com.group15.roborally.client.model.damage;

import com.group15.roborally.client.model.Command;

public enum DamageType {
    SPAM("Spam"),
    TROJAN_HORSE("Trojan Horse"),
    WORM("Worm"),
    VIRUS("Virus");

    public final String displayName;
    private Command commandCardType;

    DamageType(String displayName) {
        this.displayName = displayName;
    }
    public void setCommandCardType(Command commandCardType) {
        this.commandCardType = commandCardType;
    }
    public Command getCommandCardType() {
        return commandCardType;
    }
}
