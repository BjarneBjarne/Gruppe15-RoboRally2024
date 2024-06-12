package dk.dtu.compute.se.pisd.roborally.Board;

import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Heading;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardTest {
    Board board;
    @BeforeEach
    void setUp(){

    }
    @AfterEach
    void tearDown(){

    }

    @Test
    void getNeighbour(){
        board.getNeighbour(null, Heading.NORTH);
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
