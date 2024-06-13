package com.group15.model.damage;

import com.group15.model.Command;
import com.group15.model.CommandCard;
import com.group15.model.Player;

import java.util.ArrayList;
import java.util.List;

public class Damage {
    private final List<DamageTypeAmount> damageTypeAmountList = new ArrayList<>();

    public Damage(int spamDamageAmount, int trojanHorseDamageAmount, int wormDamageAmount, int virusDamageAmount) {
        // Initialize instances for each damage type
        damageTypeAmountList.add(new DamageTypeAmount(spamDamageAmount, DamageType.SPAM));
        damageTypeAmountList.add(new DamageTypeAmount(trojanHorseDamageAmount, DamageType.TROJAN_HORSE));
        damageTypeAmountList.add(new DamageTypeAmount(wormDamageAmount, DamageType.WORM));
        damageTypeAmountList.add(new DamageTypeAmount(virusDamageAmount, DamageType.VIRUS));
    }

    public void setAmount(DamageType damageType, int newAmount) {
        if (newAmount < 0) newAmount = 0;
        for (DamageTypeAmount damageTypeAmount : damageTypeAmountList) {
            if (damageTypeAmount.type == damageType) {
                damageTypeAmount.setAmount(newAmount);
            }
        }
    }
    public void addAmount(DamageType damageType, int amount) {
        for (DamageTypeAmount damageTypeAmount : damageTypeAmountList) {
            if (damageTypeAmount.type == damageType) {
                if (amount < 0) {
                    System.out.println("ERROR: Can't add negative amount in Damage.addAmount(). Amount gotten: " + amount + ", for damageType: " + damageType.displayName);
                    return;
                }
                damageTypeAmount.addAmount(amount);
                break;
            }
        }
    }
    public void subtractAmount(DamageType damageType, int amount) {
        for (DamageTypeAmount damageTypeAmount : damageTypeAmountList) {
            if (damageTypeAmount.type == damageType) {
                if (amount < 0) {
                    System.out.println("ERROR: Can't subtract negative amount in Damage.subtractAmount(). Amount gotten: " + amount + ", for damageType: " + damageType.displayName);
                    return;
                }
                damageTypeAmount.subtractAmount(amount);
                break;
            }
        }
    }
    public int getAmount(DamageType damageType) {
        for (DamageTypeAmount dt : damageTypeAmountList) {
            if (dt.type == damageType) {
                return dt.getAmount();
            }
        }
        System.out.println("ERROR: Couldn't find damage type: " + damageType.displayName + " : " + damageType);
        return 0;
    }
    public void add(Damage otherDamage) {
        for (DamageTypeAmount otherDamageTypeAmount : otherDamage.getDamageTypeList()) {
            addAmount(otherDamageTypeAmount.type, otherDamageTypeAmount.getAmount());
        }
    }
    public void subtract(Damage otherDamage) {
        for (DamageTypeAmount otherDamageTypeAmount : otherDamage.getDamageTypeList()) {
            subtractAmount(otherDamageTypeAmount.type, otherDamageTypeAmount.getAmount());
        }
    }
    public void clear() {
        for (DamageTypeAmount damageTypeAmount : damageTypeAmountList) {
            damageTypeAmount.setAmount(0);
        }
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
