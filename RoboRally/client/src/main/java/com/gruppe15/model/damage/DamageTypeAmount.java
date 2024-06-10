package com.gruppe15.model.damage;

public class DamageTypeAmount {
    private int amount;
    public final DamageType type;
    public DamageTypeAmount(int amount, DamageType type) {
        this.amount = amount;
        this.type = type;
    }

    public void setAmount(int newAmount) {
        this.amount = newAmount;
    }
    public void addAmount(int amount) {
        this.amount += amount;
    }
    public void subtractAmount(int amount) {
        this.amount -= amount;
    }
    public int getAmount() {
        return this.amount;
    }
    //public abstract void applyDamage(Player player); // Logic for handling damage to {player}
}
