package dk.dtu.compute.se.pisd.roborally.Space;

import com.group15.roborally.client.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

public class SpaceTest {

    private Board board;
    private Player p1;
    private Player p2;

    @BeforeEach
    void setup(){
        Space[][] spaces = new Space[10][10];
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                if(i == 4 && j == 4){
                    List<Heading> walls = new ArrayList<>();
                    walls.add(Heading.NORTH);
                    spaces[i][j] = new Space(board, i, j, null, walls);
                }else{
                    spaces[i][j] = new Space(board, i, j, null);
                }
            }
        }
        board = new Board(null, spaces, "test", 1);
        p1 = new Player(0, "p1", board, Robots.getRobotByName("SPIN BOT"));
        p2 = new Player(1, "p2", board, Robots.getRobotByName("ZOOM BOT"));
        board.addPlayer(p1);
        board.addPlayer(p2);
    }

    @AfterEach
    void taredown(){
        p1 = null;
        p2 = null;
        board = null;
    }

    @Test
    void setPlayerTest(){
        Space space = board.getSpace(0,0);
        space.setPlayer(p1);
        Assertions.assertSame(space, p1.getSpace());
        Assertions.assertSame(p1, space.getPlayer());
        space.setPlayer(p2);
        Assertions.assertNull(p1.getSpace());
        Assertions.assertSame(space, p2.getSpace());
        Assertions.assertSame(p2, space.getPlayer());

    }

    @Test
    void getDistanceFromOtherSpaceTest(){
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                int dist = board.getSpace(0,0).getDistanceFromOtherSpace(board.getSpace(i,j));
                Assertions.assertEquals((int) sqrt((i*i)+(j*j)), dist);
            }
        }
    }

    @Test
    void getIsWallBetweenTest(){
        Space space = board.getSpace(4,4);
        Assertions.assertTrue(space.getIsWallBetween(board.getSpace(4,3)));
        Assertions.assertFalse(space.getIsWallBetween(board.getSpace(4,5)));
        Assertions.assertFalse(space.getIsWallBetween(board.getSpace(3,4)));
        Assertions.assertFalse(space.getIsWallBetween(board.getSpace(5,4)));
        space = board.getSpace(4,3);
        Assertions.assertTrue(space.getIsWallBetween(board.getSpace(4,4)));
        Assertions.assertFalse(space.getIsWallBetween(board.getSpace(4,2)));
        Assertions.assertFalse(space.getIsWallBetween(board.getSpace(3,3)));
        Assertions.assertFalse(space.getIsWallBetween(board.getSpace(5,3)));
    }

    @Test
    void getDirectionToOtherSpaceTest(){
        Space space = board.getSpace(4,4);
        Assertions.assertSame(Heading.NORTH, space.getDirectionToOtherSpace(board.getSpace(4,3)));
        Assertions.assertSame(Heading.SOUTH, space.getDirectionToOtherSpace(board.getSpace(4,5)));
        Assertions.assertSame(Heading.EAST, space.getDirectionToOtherSpace(board.getSpace(5,4)));
        Assertions.assertSame(Heading.WEST, space.getDirectionToOtherSpace(board.getSpace(3,4)));
    }

    @Test
    void getSpaceNextToTest(){
        Space space = board.getSpace(4,4);
        Space[][] spaces = board.getSpaces();
        Assertions.assertSame(board.getSpace(4,3), space.getSpaceNextTo(Heading.NORTH, spaces));
        Assertions.assertSame(board.getSpace(4,5), space.getSpaceNextTo(Heading.SOUTH, spaces));
        Assertions.assertSame(board.getSpace(5,4), space.getSpaceNextTo(Heading.EAST, spaces));
        Assertions.assertSame(board.getSpace(3,4), space.getSpaceNextTo(Heading.WEST, spaces));
    }
}
