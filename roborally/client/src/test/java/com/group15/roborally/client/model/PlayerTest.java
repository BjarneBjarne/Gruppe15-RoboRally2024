package com.group15.roborally.client.model;

import com.group15.roborally.client.coursecreator.CC_CourseData;
import com.group15.roborally.client.coursecreator.CC_JsonUtil;

import com.group15.roborally.server.model.GamePhase;
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
        player = new Player(0, "ServerTest", board, robot);
    }
    @AfterEach
    void tearDown(){
        player = null;
    }

    @Test
    void moveToTempSpaceTest(){
        Space space = new Space(board,0,0,null);
        Space temp = new Space(board,1,1,null);
        player.setTemporarySpace(temp);
        player.setSpace(space);
        player.goToTemporarySpace();
        Assertions.assertEquals(player, temp.getPlayer());
        Assertions.assertNull(space.getPlayer());
    }

    @Test
    void rebootPlayerTest(){
        Space space = new Space(board,0,0,null);
        player.setSpace(space);
        board.setCurrentPhase(GamePhase.INITIALIZATION);
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
    void deafultDeckTest(){
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
                count[getCommandNr(temp.command)]++;
            }
        }
        Assertions.assertArrayEquals(facit, count);
    }

    @Test
    void drawFromDeckTest(){
        player.getProgrammingDeck().clear();
        player.getProgrammingDeck().add(new CommandCard(Command.AGAIN));
        Assertions.assertSame(Command.AGAIN, player.drawFromDeck().command);
    }

    @Test
    void drawHandTest() {
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
            count[getCommandNr(temp)]++;
        }
        Assertions.assertArrayEquals(facit, count);
    }

    @Test
    void discardAllTest(){
        int[] count = new int[10];
        int[] facit = {4,3,1,4,4,1,1,1,1,1};
        player.setProgrammingDeckToDefault();
        player.drawHand();
        player.fillRestOfRegisters();
        player.discardAll();
        Queue<CommandCard> deck = player.getProgrammingDeck();
        int size = deck.size();
        Assertions.assertEquals(21, size);
        for(int i = 0; i < size; i++){
            CommandCard temp = deck.poll();
            if(temp == null){
                count[9]++;
            }else {
                count[getCommandNr(temp.command)]++;
            }
        }
        Assertions.assertArrayEquals(facit, count);
    }

    @Test
    void queueCommandTest(){
        int before = player.getEnergyCubes();
        player.queueCommand(Command.POWER_UP, null);
        int after = player.getEnergyCubes();
        Assertions.assertTrue(before+1 == after);
    }

    private int getCommandNr(Command cmd){
        if(cmd == null){
            return 9;
        }else {
            switch (cmd) {
                case Command.MOVE_1:
                    return 0;
                case Command.MOVE_2:
                    return 1;
                case Command.MOVE_3:
                    return 2;
                case Command.RIGHT_TURN:
                    return 3;
                case Command.LEFT_TURN:
                    return 4;
                case Command.U_TURN:
                    return 5;
                case Command.MOVE_BACK:
                    return 6;
                case Command.POWER_UP:
                    return 7;
                case Command.AGAIN:
                    return 8;
            }
        }
        return -1;
    }
}
