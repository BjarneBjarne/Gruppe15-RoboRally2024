package dk.dtu.compute.se.pisd.roborally.UpgradeShop;

import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.UpgradeShop;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpgradeShopTest{

    private UpgradeShop upgradeShop;
    private Board board;

    @BeforeEach
    void setup(){
        Board board = new Board(null, null, "test", 1);
        upgradeShop = new UpgradeShop(board);
    }

    @AfterEach
    void teardown(){
        upgradeShop = null;
    }

    @Test
    void attemptBuyCardFromShop(){
        upgradeShop.attemptBuyCardFromShop(null, null);
    }

    @Test
    void attemptReceiveFreeCardFromShop(){
        upgradeShop.attemptReceiveFreeCardFromShop(null, null);
    }

    @Test
    void refillAvailableCards(){
        upgradeShop.refillAvailableCards();
    }

    @Test
    void update(){
        upgradeShop.update(null);
    }
}