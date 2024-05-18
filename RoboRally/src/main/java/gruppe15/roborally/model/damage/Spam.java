package gruppe15.roborally.model.damage;

import gruppe15.roborally.model.Command;
import gruppe15.roborally.model.CommandCard;
import gruppe15.roborally.model.Player;

public class Spam extends DamageType {

    public Spam(int amount) {
        super(amount, DamageTypes.SPAM);
    }

    /*@Override
    public void applyDamage(Player player) {

    }*/
}
