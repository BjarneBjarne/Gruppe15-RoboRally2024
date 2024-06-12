package dk.dtu.compute.se.pisd.roborally.player;

import gruppe15.roborally.coursecreator.CC_CourseData;
import gruppe15.roborally.coursecreator.CC_JsonUtil;
import gruppe15.roborally.model.*;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Queue;

public class PlayerTest {
    private Player player;
    private Board board;

    @BeforeEach
    void setUp(){
        Robots robot = Robots.getRobotByName("SPIN BOT");
        board = new Board(null, new Space[10][10], null, 4);
        player = new Player(board, robot, "test");
    }
    @AfterEach
    void tearDown(){
        player = null;
    }

    @Test
    void moveToTempSpace(){
        Space space = new Space(board,0,0,null);
        Space temp = new Space(board,1,1,null);
        player.setTemporarySpace(temp);
        player.setSpace(space);
        player.goToTemporarySpace();
        Assertions.assertEquals(player, temp.getPlayer());
        Assertions.assertNull(space.getPlayer());
    }

    @Test
    void rebootPlayer(){
        Space space = new Space(board,0,0,null);
        player.setSpace(space);
        board.setCurrentPhase(Phase.INITIALIZATION);
        player.setProgrammingDeckToDefault();
        player.startRebooting(null, true);
        Queue<CommandCard> deck = player.getProgrammingDeck();
        boolean passed = false;
        int size = deck.size();
        for(int i = 0; i < size; i++){
            CommandCard temp = deck.poll();
            if(temp != null && temp.command.equals(Command.SPAM)){
                passed = true;
            }
        }
        Assertions.assertTrue(passed);

    }

    @Test
    void deafultDeck(){
        int[] count = new int[10];
        int[] facit = {4,3,1,4,4,1,1,1,1,1};
        player.setProgrammingDeckToDefault();
        Queue<CommandCard> deck = player.getProgrammingDeck();
        int size = deck.size();
        Assertions.assertEquals(21, size);
        for(int i = 0; i < size; i++){
            CommandCard temp = deck.poll();
            if(temp == null){
                count[9]++;
            }else {
                switch (temp.command) {
                    case Command.MOVE_1:
                        count[0]++;
                        break;
                    case Command.MOVE_2:
                        count[1]++;
                        break;
                    case Command.MOVE_3:
                        count[2]++;
                        break;
                    case Command.RIGHT_TURN:
                        count[3]++;
                        break;
                    case Command.LEFT_TURN:
                        count[4]++;
                        break;
                    case Command.U_TURN:
                        count[5]++;
                        break;
                    case Command.MOVE_BACK:
                        count[6]++;
                        break;
                    case Command.POWER_UP:
                        count[7]++;
                        break;
                    case Command.AGAIN:
                        count[8]++;
                        break;
                }
            }
        }
        Assertions.assertArrayEquals(facit, count);
    }

    @Test
    void drawFromDeck(){
        player.getProgrammingDeck().add(new CommandCard(Command.AGAIN));
        Assertions.assertSame(player.drawFromDeck().command, Command.AGAIN);
    }

    @Test
    void drawHand() {
        int[] count = new int[10];
        int[] facit = {4, 3, 1, 4, 4, 1, 1, 1, 1, 1};
        player.setProgrammingDeckToDefault();
        player.drawHand();
        Queue<CommandCard> deck = player.getProgrammingDeck();
        CardField[] hand = player.getCardHandFields();
        int size = deck.size();
        Assertions.assertEquals(21 - player.getMaxNoOfCardsInHand(), size);
        int j = 0;
        for (int i = 0; i < 21; i++) {
            Command temp = null;
            if (deck.isEmpty()) {
                temp = ((CommandCard) hand[j].getCard()).command;
                j++;
            } else {
                CommandCard temp2 = deck.poll();
                if(temp2 == null){
                    temp = null;
                }else{
                    temp = temp2.command;
                }
            }
            if (temp == null) {
                count[9]++;
            } else {
                switch (temp) {
                    case Command.MOVE_1:
                        count[0]++;
                        break;
                    case Command.MOVE_2:
                        count[1]++;
                        break;
                    case Command.MOVE_3:
                        count[2]++;
                        break;
                    case Command.RIGHT_TURN:
                        count[3]++;
                        break;
                    case Command.LEFT_TURN:
                        count[4]++;
                        break;
                    case Command.U_TURN:
                        count[5]++;
                        break;
                    case Command.MOVE_BACK:
                        count[6]++;
                        break;
                    case Command.POWER_UP:
                        count[7]++;
                        break;
                    case Command.AGAIN:
                        count[8]++;
                        break;
                }
            }

        }
        Assertions.assertArrayEquals(facit, count);
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
