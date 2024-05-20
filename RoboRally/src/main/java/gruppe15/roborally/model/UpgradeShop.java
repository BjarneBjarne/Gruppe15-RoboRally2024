package gruppe15.roborally.model;

import gruppe15.roborally.model.upgrades.UpgradeCard;
import gruppe15.roborally.model.upgrades.UpgradeCards;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.LinkedList;

public class UpgradeShop {
    private final LinkedList<UpgradeCard> upgradeCards;
    private final int numberOfPlayers;
    transient private final CardField[] cardFields;
    private final UpgradeCard[] availableUpgradeCards;
    public UpgradeShop(int numberOfPlayers) {
        this.upgradeCards = getAllUpgradeCards();
        this.numberOfPlayers = numberOfPlayers;
        this.cardFields = new CardField[numberOfPlayers];
        this.availableUpgradeCards = new UpgradeCard[numberOfPlayers];

        for (int i = 0; i < numberOfPlayers; i++) {
            cardFields[i] = new CardField(this);
        }
    }
    public void refreshUpgradeShopCards() {
        Collections.shuffle(upgradeCards);

        for (int i = 0; i < numberOfPlayers; i++) {
            availableUpgradeCards[i] = upgradeCards.poll();
            cardFields[i].setCard(availableUpgradeCards[i]);
        }
    }
    public UpgradeCard[] getAvailableUpgradeCards() {
        return availableUpgradeCards;
    }
    public void removeCard(UpgradeCard upgradeCard) {
        upgradeCards.remove(upgradeCard);
    }
    public CardField getCardField(int index) {
        return cardFields[index];
    }

    private LinkedList<UpgradeCard> getAllUpgradeCards() {
        LinkedList<UpgradeCard> allUpgradeCards = new LinkedList<>();
        for (UpgradeCards upgradeCard : UpgradeCards.values()) {
            addUpgradeCard(allUpgradeCards, upgradeCard.upgradeCardClass);
        }

        return allUpgradeCards;
    }

    private static void addUpgradeCard(LinkedList<UpgradeCard> upgradeCardList, Class<? extends UpgradeCard> upgradeCardClass) {
        try {
            Constructor<? extends UpgradeCard> constructor = upgradeCardClass.getConstructor();
            UpgradeCard instance = constructor.newInstance();
            upgradeCardList.add(instance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    private static void addNOfUpgradeCard(LinkedList<UpgradeCard> upgradeCardList, Class<? extends UpgradeCard> upgradeCardClass, int count) {
        Constructor<? extends UpgradeCard> constructor;
        try {
            constructor = upgradeCardClass.getConstructor();
            upgradeCardList.addAll(Collections.nCopies(count, constructor.newInstance()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
