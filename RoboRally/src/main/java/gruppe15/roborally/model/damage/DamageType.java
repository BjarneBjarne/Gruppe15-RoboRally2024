package gruppe15.roborally.model.damage;

import gruppe15.roborally.model.Player;

public abstract class DamageType {
    private int amount;
    public final DamageTypes damageType;
    public DamageType(int amount, DamageTypes damageType) {
        this.amount = amount;
        this.damageType = damageType;
    }

    public void setAmount(int newAmount) {
        this.amount = newAmount;
    }
    public int getAmount() {
        return this.amount;
    }
    //public abstract void applyDamage(Player player); // Logic for handling damage to {player}
}