package gruppe15.roborally.model.damage;

import gruppe15.roborally.model.Command;
import gruppe15.roborally.model.CommandCard;
import gruppe15.roborally.model.Player;

import java.util.ArrayList;
import java.util.List;

public class Damage {
    private final List<DamageType> damageTypes = new ArrayList<>();

    public Damage(int spamDamage, int trojanHorseDamage, int wormDamage, int virusDamage) {
        // Initialize instances for each damage type
        damageTypes.add(new Spam(spamDamage));
        damageTypes.add(new TrojanHorse(trojanHorseDamage));
        damageTypes.add(new Worm(wormDamage));
        damageTypes.add(new Virus(virusDamage));
    }

    public void setAmount(Class<? extends DamageType> damageType, int newAmount) {
        for (DamageType dt : damageTypes) {
            if (dt.getClass().isAssignableFrom(damageType)) {
                dt.setAmount(newAmount);
            }
        }
    }
    public int getAmount(Class<? extends DamageType> damageType) {
        for (DamageType dt : damageTypes) {
            if (dt.getClass().isAssignableFrom(damageType)) {
                return dt.getAmount();
            }
        }
        System.out.println("ERROR: Couldn't find damage type: " + damageType.getName() + " : " + damageType);
        return 0;
    }

    public List<DamageType> getDamageTypes() {
        return damageTypes;
    }

    public void applyDamage(Player playerTakingDamage, Player playerInflictingTheDamage) {
        for (DamageType damageType : damageTypes) {
            for (int i = 0; i < damageType.getAmount(); i++) {
                playerTakingDamage.addCardToDeck(new CommandCard(damageType.damageType.getCommandCardType()));
            }
            // Print the damage dealt
            if (playerInflictingTheDamage != null) {
                System.out.println("Player {" + playerInflictingTheDamage.getName() + "} dealt " + damageType.getAmount() + " " + damageType.damageType + " damage to player {" + playerTakingDamage.getName() + "}");
            } else {
                System.out.println("Board laser dealt " + damageType.getAmount() + " " + damageType.damageType + " damage to player {" + playerTakingDamage.getName() + "}");
            }
            //damageType.applyDamage(player);
        }
    }
}
