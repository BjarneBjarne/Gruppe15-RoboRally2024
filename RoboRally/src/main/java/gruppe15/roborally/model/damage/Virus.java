package gruppe15.roborally.model.damage;

import gruppe15.roborally.model.Player;

public class Virus extends DamageType {
    public Virus(int amount) {
        super(amount, "Virus");
    }

    @Override
    public void applyDamage(Player player) {

    }
}
