package dk.dtu.compute.se.pisd.roborally.Board;

import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.boardelements.BE_Antenna;
import gruppe15.roborally.model.boardelements.BE_Checkpoint;
import gruppe15.roborally.model.boardelements.BE_Hole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
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
        board = new Board(subSpaces, spaces, "test", 1);
    }
    @AfterEach
    void tearDown(){
        board = null;
        spaces = null;
    }

    @Test
    void getNeighbour(){
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
    void updatePriorityList(){
        board.updatePriorityList();
    }

    @Test
    void getPlayerDistance(){
        board.getPlayerDistance(null, null);
    }

    @Test
    void findAntenna(){
        board.findAntenna();
    }

    @Test
    void getSubBoardOfSpace(){
        board.getSubBoardOfSpace(null);
    }

}
