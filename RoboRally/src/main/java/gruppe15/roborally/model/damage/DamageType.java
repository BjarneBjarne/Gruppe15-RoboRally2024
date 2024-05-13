package gruppe15.roborally.model.damage;

import gruppe15.roborally.model.Player;

public abstract class DamageType {
    private int amount;
    private final String damageName;
    public DamageType(int amount, String damageName) {
        this.amount = amount;
        this.damageName = damageName;
    }

    public void setAmount(int newAmount) {
        this.amount = newAmount;
    }
    public int getAmount() {
        return this.amount;
    }
    public String getDamageName() {
        return this.damageName;
    }
    public abstract void applyDamage(Player player); // Logic for handling damage to {player}
}