package gruppe15.roborally.model;

import gruppe15.observer.Observer;
import gruppe15.observer.Subject;
import gruppe15.roborally.model.upgrade_cards.UpgradeCard;
import gruppe15.roborally.model.upgrade_cards.UpgradeCards;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * The UpgradeShop handles transactions of UpgradeCards for EnergyCubes.
   ---
 * UpgradeCards cycle between 4 lists/places:
 * 1: They are created and placed into UpgradeShop.upgradeCardsDeck.
 * 2: When the Phase.UPGRADE starts, a number of UpgradeCards are placed in UpgradeShop.availableCardsFields.card.
 * 3: When bought, they are moved to Player.permanentUpgradeCardFields or Player.temporaryUpgradeCardFields.
 * 4: When players discard an UpgradeCard or no cards were bought the previous round, card are placed in upgradeCardsDiscardDeck.
   ---
 * The number of cards available for purchase will always try to maintain being equal to the number of players.
 * If no cards were bought the previous round, all previous available cards will first be discarded, before offering new ones.
 * When the Phase.UPGRADE starts, the shop will offer a number of new cards corresponding to (the noOfPlayers on the board) minus (the-
       number of "available cards missing").
 * If the main deck "upgradeCardsDeck" runs out of cards, the discarded cards in "upgradeCardsDiscardDeck" are shuffled and added back-
       into "upgradeCardsDeck".
 */
public class UpgradeShop implements Observer {
    private final LinkedList<UpgradeCard> upgradeCardsDeck = new LinkedList<>();
    private final LinkedList<UpgradeCard> upgradeCardsDiscardDeck = new LinkedList<>();
    transient private final CardField[] availableCardsFields;
    private final Board board;
    private int noOfPlayers;
    private int energyLevel = 0; // Variable just for the fun of it. Maybe it could be used for something, like an event at a certain energy level.

    public UpgradeShop(Board board) {
        this.board = board;
        this.noOfPlayers = board.getNoOfPlayers();
        this.availableCardsFields = new CardField[noOfPlayers];
        addAllUpgradeCardsShuffled();

        for (int i = 0; i < noOfPlayers; i++) {
            availableCardsFields[i] = new CardField(this);
        }

        board.attach(this);
    }

    /**
     * Method for getting the shops CardsFields.
     * @param index
     * @return
     */
    public CardField getAvailableCardsField(int index) {
        return availableCardsFields[index];
    }
    public CardField[] getAvailableCardsFields() {
        return availableCardsFields;
    }

    // Methods for access of transactions with shop.
    /**
     * Method for players so buy an UpgradeCard.
     * @param shopField The CardField in the shop, containing the wanted UpgradeCard.
     * @param player The player buying the card. Used for checking and setting EnergyCubes.
     * @return Returns the purchased UpgradeCard. Return NULL if the CardField isn't in the shop or if the player doesn't have enough energy cubes.
     */
    public UpgradeCard attemptBuyCardFromShop(CardField shopField, Player player) {
        // Purchase criteria checks.
        if (!Arrays.stream(availableCardsFields).toList().contains(shopField)) return null; // FAILED PURCHASE - The CardField isn't in the shop.
        UpgradeCard cardToSell = (UpgradeCard) shopField.getCard();
        if (player.getEnergyCubes() < cardToSell.getPurchaseCost()) return null; // FAILED PURCHASE - The player doesn't have enough energy cubes.
        // Completing transaction
        player.setEnergyCubes(player.getEnergyCubes() - cardToSell.getPurchaseCost());
        energyLevel += cardToSell.getPurchaseCost();
        shopField.setCard(null);
        return cardToSell; // SUCCESSFUL PURCHASE - Transaction complete. Sending UpgradeCard to buyer method.
    }

    public UpgradeCard attemptReceiveFreeCardFromShop(Class<? extends UpgradeCard> upgradeCardClass, Player player) {
        // Available cards
        for (CardField shopField : availableCardsFields) {
            UpgradeCard cardToGive = (UpgradeCard) shopField.getCard();
            if (cardToGive.getClass().equals(upgradeCardClass)) {
                shopField.setCard(drawCard()); // Refilling the shop field, since this method isn't supposed to be called during gameplay.
                return cardToGive; // SUCCESSFUL PURCHASE - Upgrade card in shop. Sending UpgradeCard to receiver method.
            }
        }
        // Card deck
        for (UpgradeCard cardInDeck : upgradeCardsDeck) {
            if (cardInDeck.getClass().equals(upgradeCardClass)) {
                upgradeCardsDeck.remove(cardInDeck);
                return cardInDeck; // SUCCESSFUL PURCHASE - Upgrade card in shop deck. Sending UpgradeCard to receiver method.
            }
        }
        // Discard card deck
        for (UpgradeCard cardInDiscardDeck : upgradeCardsDiscardDeck) {
            if (cardInDiscardDeck.getClass().equals(upgradeCardClass)) {
                upgradeCardsDeck.remove(cardInDiscardDeck);
                return cardInDiscardDeck; // SUCCESSFUL PURCHASE - Upgrade card in shop discard deck. Sending UpgradeCard to receiver method.
            }
        }
        return null; // FAILED PURCHASE - The CardField isn't in the shop.
    }

    /**
     * Method for players to return a previously purchased UpgradeCard to the shop pool.
     * @param upgradeCard The UpgradeCard to return to the shop.
     */
    public void returnCardToShop(UpgradeCard upgradeCard) {
        upgradeCardsDiscardDeck.offerLast(upgradeCard);
    }


    // Methods for refreshing thw cards available for purchase.
    /**
     * Method for refilling the shop. Check the UpgradeShop class description for more info.
     */
    public void refillAvailableCards() {
        // If no cards were bought, discard all cards
        if (getNoOfMissingCards() == 0) {
            System.out.println("No cards bought. Refreshing whole shop.");
            for (int i = 0; i < noOfPlayers; i++) {
                upgradeCardsDiscardDeck.offerLast((UpgradeCard) availableCardsFields[i].getCard());
                availableCardsFields[i].setCard(null);
            }
        }
        // Add noOfMissing cards to availableUpgradeCards
        for (int i = 0; i < noOfPlayers; i++) {
            if (availableCardsFields[i].getCard() == null) {
                availableCardsFields[i].setCard(drawCard());
            }
        }
    }
    /**
     * Method for getting the number of cards needed to satisfy shop stock rules. Check the UpgradeShop class description for more info.
     * @return
     */
    private int getNoOfMissingCards() {
        int noOfMissingCards = 0;
        for (int i = 0; i < noOfPlayers; i++) {
            if (availableCardsFields[i].getCard() == null) {
                noOfMissingCards++;
            }
        }
        return noOfMissingCards;
    }

    public int getNoOfAvailableCards() {
        int noOfAvailableCards = 0;
        for (CardField cardField : availableCardsFields) {
            UpgradeCard cardFieldCard = (UpgradeCard) cardField.getCard();
            if (cardFieldCard != null) {
                noOfAvailableCards++;
            }
        }
        return noOfAvailableCards;
    }

    // Methods for card draws.
    /**
     * Method for drawing a new UpgradeCard from the main shop deck "upgradeCardsDeck".
     * @return
     */
    private @Nullable UpgradeCard drawCard() {
        UpgradeCard drawnCard = upgradeCardsDeck.pollFirst();
        if (drawnCard != null) {
            return drawnCard;
        } else {
            if (!upgradeCardsDiscardDeck.isEmpty()) { // If there are old unused cards in the discard deck.
                shuffleDiscardDeckToDeck();
                if (!upgradeCardsDeck.isEmpty()) {
                    return drawCard();
                }
            }
        }
        System.out.println("Shop ran out of cards to draw from deck. :(");
        return null;
    }
    /**
     * Used for shuffling the DiscardDeck back into the main deck, when the main deck runs our of cards.
     */
    private void shuffleDiscardDeckToDeck() {
        //System.out.println("Shuffling shop discard deck");
        Collections.shuffle(upgradeCardsDiscardDeck);
        for (int i = 0; i < upgradeCardsDiscardDeck.size(); i++) {
            upgradeCardsDeck.offerLast(upgradeCardsDiscardDeck.pollFirst());
        }
    }


    // Methods for creation of cards
    /**
     * Method for adding one of each UpgradeCard to the main deck.
     */
    private void addAllUpgradeCardsShuffled() {
        for (UpgradeCards upgradeCard : UpgradeCards.values()) {
            upgradeCardsDeck.add(UpgradeCard.getUpgradeCardFromClass(upgradeCard.upgradeCardClass));
        }
        Collections.shuffle(upgradeCardsDeck);
    }



    // Listener method for ensuring variables are up-to-date.
    @Override
    public void update(Subject subject) {
        if (subject == board) {
            this.noOfPlayers = board.getNoOfPlayers();
        }
    }
}
