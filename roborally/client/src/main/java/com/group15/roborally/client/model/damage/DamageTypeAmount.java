package com.group15.roborally.client.model.damage;

import lombok.Getter;
import lombok.Setter;

public class DamageTypeAmount {
    @Getter
    @Setter
    private int amount;
    public final DamageType type;
    public DamageTypeAmount(int amount, DamageType type) {
        this.amount = amount;
        this.type = type;
    }

    public void addAmount(int amount) {
        this.amount += amount;
    }
    public void subtractAmount(int amount) {
        this.amount -= amount;
    }
}
