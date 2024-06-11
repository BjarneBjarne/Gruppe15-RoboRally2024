package dk.dtu.compute.se.pisd.roborally.player;

import gruppe15.roborally.coursecreator.CC_CourseData;
import gruppe15.roborally.coursecreator.CC_JsonUtil;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Robots;
import gruppe15.roborally.model.Space;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp(){
        List<CC_CourseData> courses = CC_JsonUtil.getCoursesInFolder("courses");
        CC_CourseData courseData = courses.get(0);
        Pair<List<Space[][]>, Space[][]> courseSpaces = courseData.getGameSubBoards();
        Board board = new Board(courseSpaces.getKey(), courseSpaces.getValue(), courseData.getCourseName(), courseData.getNoOfCheckpoints());
        Robots robot = Robots.getRobotByName("SPIN BOT");
        player = new Player(board, robot, "test");
    }
    @AfterEach
    void tearDown(){
        player = null;
    }

    @Test
    void moveToTempSpace(){
        player.goToTemporarySpace();
    }

    @Test
    void rebootPlayer(){
        player.startRebooting(null, false);
        player.stopRebooting();
    }

    @Test
    void buyUpgrade(){
        player.attemptUpgradeCardPurchase(null, null);
    }

    @Test
    void addFreeUpgradeCard(){
        player.tryAddFreeUpgradeCard(null, null, 0);
    }

    @Test
    void removeUpgradeCard(){
        player.removeUpgradeCard(null);
    }

    @Test
    void deafultDeck(){
        player.setProgrammingDeckToDefault();
    }

    @Test
    void drawFromDeck(){
        player.drawFromDeck();
    }

    @Test
    void drawHand(){
        player.drawHand();
    }

    @Test
    void discardAll(){
        player.discardAll();
    }

    @Test
    void discardProgram(){
        player.discardProgram();
    }

    @Test
    void discardHand(){
        player.discardHand();
    }

    @Test
    void fillRestOfRegisters(){
        player.fillRestOfRegisters();
    }

    @Test
    void queueCommand(){
        player.queueCommand(null, null);
    }

    @Test
    void startMovement(){
        player.startMovement(null);
    }

    @Test
    void turn(){
        player.turn(1);
    }

    @Test
    void shootLaser(){
        player.shootLaser(null);
    }

    @Test
    void useTemporaryBonusDamage(){
        player.useTemporaryBonusDamage();
    }
}
