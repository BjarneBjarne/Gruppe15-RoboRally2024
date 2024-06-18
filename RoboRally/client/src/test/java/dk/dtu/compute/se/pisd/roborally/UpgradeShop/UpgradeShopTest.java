package dk.dtu.compute.se.pisd.roborally.UpgradeShop;

import gruppe15.roborally.model.*;
import gruppe15.roborally.model.upgrade_cards.UpgradeCard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpgradeShopTest{

    private Player p1;
    private Player p2;
    private Board board;
    private UpgradeShop upgradeShop;

    @BeforeEach
    void setup(){
        board = new Board(null, new Space[10][10], "test", 1);
        p1 = new Player(board, Robots.getRobotByName("SPIN BOT"), "p1");
        p2 = new Player(board, Robots.getRobotByName("ZOOM BOT"), "p2");
        board.addPlayer(p1);
        board.addPlayer(p2);
        upgradeShop = new UpgradeShop(board);
    }

    @AfterEach
    void teardown(){
        p1 = null;
        p2 = null;
        board = null;
        upgradeShop = null;
    }

    @Test
    void attemptBuyCardFromShopTest(){
        upgradeShop.refillAvailableCards();
        CardField upgradeCardField = upgradeShop.getAvailableCardsField(1);
        UpgradeCard upgradeCard = (UpgradeCard) upgradeCardField.getCard();
        p1.setEnergyCubes(upgradeCard.getPurchaseCost());
        UpgradeCard result = upgradeShop.attemptBuyCardFromShop(upgradeCardField, p1);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, p1.getEnergyCubes());
        Assertions.assertSame(upgradeCard, result);
    }

    @Test
    void attemptReceiveFreeCardFromShopTest(){
        upgradeShop.refillAvailableCards();
        UpgradeCard upgradeCard = (UpgradeCard) upgradeShop.getAvailableCardsField(0).getCard();
        Assertions.assertSame(upgradeCard, upgradeShop.attemptReceiveFreeCardFromShop(upgradeCard.getClass(), p1));
    }

    @Test
    void refillAvailableCardsTest(){
        CardField[] cardFields = upgradeShop.getAvailableCardsFields();
        Assertions.assertEquals(2, cardFields.length);
        Assertions.assertNull(cardFields[0].getCard());
        Assertions.assertNull(cardFields[1].getCard());
        upgradeShop.refillAvailableCards();
        Assertions.assertNotNull(cardFields[0].getCard());
        Assertions.assertNotNull(cardFields[1].getCard());
    }
}