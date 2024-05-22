package gruppe15.roborally.model.damage;

import gruppe15.roborally.model.Command;
import gruppe15.roborally.model.CommandCard;
import gruppe15.roborally.model.Player;

import java.util.ArrayList;
import java.util.List;

public class Damage {
    private final List<DamageTypeAmount> damageTypeAmountList = new ArrayList<>();

    public Damage(int spamDamageAmount, int trojanHorseDamageAmount, int wormDamageAmount, int virusDamageAmount) {
        // Initialize instances for each damage type
        damageTypeAmountList.add(new DamageTypeAmount(spamDamageAmount, DamageTypes.SPAM));
        damageTypeAmountList.add(new DamageTypeAmount(trojanHorseDamageAmount, DamageTypes.TROJAN_HORSE));
        damageTypeAmountList.add(new DamageTypeAmount(wormDamageAmount, DamageTypes.WORM));
        damageTypeAmountList.add(new DamageTypeAmount(virusDamageAmount, DamageTypes.VIRUS));
    }

    public void setAmount(DamageTypes damageType, int newAmount) {
        for (DamageTypeAmount dt : damageTypeAmountList) {
            if (dt.type == damageType) {
                dt.setAmount(newAmount);
            }
        }
    }
    public int getAmount(DamageTypes damageType) {
        for (DamageTypeAmount dt : damageTypeAmountList) {
            if (dt.type == damageType) {
                return dt.getAmount();
            }
        }
        System.out.println("ERROR: Couldn't find damage type: " + damageType.displayName + " : " + damageType);
        return 0;
    }

    public List<DamageTypeAmount> getDamageTypeList() {
        return damageTypeAmountList;
    }

    public void applyDamage(Player playerTakingDamage, Player playerInflictingTheDamage) {
        for (DamageTypeAmount damageTypeAmount : damageTypeAmountList) {
            Command cmdType = damageTypeAmount.type.getCommandCardType();
            if (damageTypeAmount.getAmount() > 0) {
                for (int i = 0; i < damageTypeAmount.getAmount(); i++) {
                    playerTakingDamage.discard(new CommandCard(cmdType));
                }
                // Print the damage dealt
                /*if (playerInflictingTheDamage != null) {
                    System.out.println("Player {" + playerInflictingTheDamage.getName() + "} dealt " + damageTypeAmount.getAmount() + " " + damageTypeAmount.type + " damage to player {" + playerTakingDamage.getName() + "}");
                } else {
                    System.out.println("Board laser dealt " + damageTypeAmount.getAmount() + " " + damageTypeAmount.type + " damage to player {" + playerTakingDamage.getName() + "}");
                }*/
                //damageTypeAmount.applyDamage(player);
            }
        }
    }
}
