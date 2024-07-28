package com.group15.roborally.client.model;

import com.group15.roborally.client.LobbySettings;
import com.group15.roborally.client.model.boardelements.BE_Antenna;
import com.group15.roborally.client.model.boardelements.BE_Checkpoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BoardTest {
    Board board;
    Space[][] spaces;
    List<Space[][]> subSpaces;

    @BeforeEach
    void setUp(){
        spaces = new Space[10][10];
        Space[][] sub1 = new Space[5][5];
        Space[][] sub2 = new Space[5][5];
        subSpaces = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                Space space = new Space(board, i, j, null);
                if(i == 9 && j == 9){
                    space = new Space(board, 9, 9, new BE_Antenna(null));
                }else if(i == 0 && j == 0){
                    space = new Space(board, 0, 0, new BE_Checkpoint(1));
                }
                spaces[i][j] = space;
                if(i < 5 && j < 5){
                    sub1[i][j] = space;
                }else if(i >= 5 && j >= 5){
                    sub2[i-5][j-5] = space;
                }
            }
        }
        subSpaces.add(sub1);
        subSpaces.add(sub2);
        board = new Board(subSpaces, spaces, "ServerTest", 1);
    }
    @AfterEach
    void tearDown(){
        board = null;
        spaces = null;
    }

    @Test
    void getNeighbourTest(){
        Space space = board.getNeighbour(spaces[0][0], Heading.SOUTH);
        Assertions.assertSame(space, spaces[0][1]);
        space = board.getNeighbour(spaces[1][0], Heading.WEST);
        Assertions.assertSame(space, spaces[0][0]);
        space = board.getNeighbour(spaces[0][2], Heading.NORTH);
        Assertions.assertSame(space, spaces[0][1]);
        space = board.getNeighbour(spaces[0][0], Heading.EAST);
        Assertions.assertSame(space, spaces[1][0]);

    }

    @Test
    void updatePriorityListTest(){
        Player p1 = new Player(0, "p1", board, Robots.getRobotByName("SPIN BOT"));
        Player p2 = new Player(1, "p2", board, Robots.getRobotByName("ZOOM BOT"));
        board.addPlayer(p1);
        board.addPlayer(p2);
        p1.setSpace(spaces[7][5]);
        p2.setSpace(spaces[3][1]);
        LobbySettings.NO_OF_PLAYERS = 2;
        board.updatePriorityList();
        Assertions.assertEquals(0, p1.getPriority());
        Assertions.assertEquals(1, p2.getPriority());
    }

    @Test
    void getPlayerDistanceTest(){
        Player p1 = new Player(0, "p1", board, Robots.getRobotByName("SPIN BOT"));
        board.addPlayer(p1);
        p1.setSpace(spaces[1][1]);
        LobbySettings.NO_OF_PLAYERS = 1;
        int d = board.getPlayerDistance(p1, spaces[9][9]);
        Assertions.assertEquals(16, d);
    }

    @Test
    void findAntennaTest(){
        Space antenna = board.findAntenna();
        Assertions.assertSame(spaces[9][9], antenna);
    }

    @Test
    void getSubBoardOfSpaceTest(){
        Space[][] sub1 = board.getSubBoardOfSpace(spaces[2][3]);
        Space[][] sub2 = board.getSubBoardOfSpace(spaces[6][8]);
        Assertions.assertSame(subSpaces.get(0), sub1);
        Assertions.assertSame(subSpaces.get(1), sub2);
    }

}
