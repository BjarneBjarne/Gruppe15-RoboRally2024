package gruppe15.roborally.model.damage;

import gruppe15.roborally.model.Player;

public abstract class DamageType {
    private int amount;
    public DamageType(int amount) {
        this.amount = amount;
    }

    public void setAmount(int newAmount) {
        this.amount = newAmount;
    }
    public int getAmount() {
        return this.amount;
    }
    public abstract void applyDamage(Player player); // Logic for handling damage to {player}
}