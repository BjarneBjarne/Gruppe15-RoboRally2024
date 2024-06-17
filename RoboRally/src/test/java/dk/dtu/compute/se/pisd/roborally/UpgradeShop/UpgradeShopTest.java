package dk.dtu.compute.se.pisd.roborally.UpgradeShop;

import gruppe15.roborally.model.*;
import gruppe15.roborally.model.upgrade_cards.UpgradeCard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpgradeShopTest{

    @Test
    void attemptBuyCardFromShop(){
        Board board = new Board(null, null, "test", 1);
        Player p1 = new Player(board, Robots.getRobotByName("SPIN BOT"), "p1");
        Player p2 = new Player(board, Robots.getRobotByName("ZOOM BOT"), "p2");
        board.addPlayer(p1);
        board.addPlayer(p2);
        UpgradeShop upgradeShop = new UpgradeShop(board);
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
    void attemptReceiveFreeCardFromShop(){
        Board board = new Board(null, null, "test", 1);
        UpgradeShop upgradeShop = new UpgradeShop(board);
        upgradeShop.attemptReceiveFreeCardFromShop(null, null);
    }

    @Test
    void refillAvailableCards(){
        Board board = new Board(null, null, "test", 1);
        UpgradeShop upgradeShop = new UpgradeShop(board);
        upgradeShop.refillAvailableCards();
    }

    @Test
    void update(){
        Board board = new Board(null, null, "test", 1);
        UpgradeShop upgradeShop = new UpgradeShop(board);
        upgradeShop.update(null);
    }
}