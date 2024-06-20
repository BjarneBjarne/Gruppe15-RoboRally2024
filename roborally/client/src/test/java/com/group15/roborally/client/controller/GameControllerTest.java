package com.group15.roborally.client.controller;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

import static com.group15.roborally.client.model.Phase.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameControllerTest {
    private Board board;
    private Runnable gameOverMethod;
    private GameController gameController;
    private Player player1;
    private Player player2;
    private Queue<Player> mockPriorityList;

    @BeforeEach
    void setUp() {
        // board = mock(Board.class);
        // gameOverMethod = mock(Runnable.class);
        // player1 = mock(Player.class);
        // player2 = mock(Player.class);
        // mockPriorityList = mock(Queue.class);
        // gameController = new GameController(board, );
    }

    @Test
    public void startProgrammingPhaseTest() {

        // AtomicReference<Phase> currentPhase = new AtomicReference<>();
        // doAnswer(invocation -> {
        //     currentPhase.set(invocation.getArgument(0));
        //     return null;
        // }).when(board).setCurrentPhase(any(Phase.class));
        // when(board.getCurrentPhase()).thenAnswer(invocation -> currentPhase.get());

        // when(mockPriorityList.peek()).thenReturn(player1);
        // when(board.getPriorityList()).thenReturn(mockPriorityList);

        // gameController.startProgrammingPhase();

        // assertEquals(PROGRAMMING, board.getCurrentPhase());
    }


}



/*    @Test
    void testExecutePrograms() {
        gameController.executePrograms();

        verify(board).setStepMode(false);
        verify(gameController).handlePlayerRegister();
    }
}



    /*    @Test
    void testStartProgrammingPhase() {

        when(board.getPlayers()).thenReturn(List.of(player1, player2));
        when(board.getNoOfPlayers()).thenReturn(2);

        gameController.startProgrammingPhase();

        verify(board).setCurrentPhase(PROGRAMMING);
        verify(board).setCurrentRegister(0);
        verify(player1).discardAll();
        verify(player2).discardAll();
        verify(player1, times(NO_OF_CARDS_IN_HAND)).drawHand();
        verify(player2, times(NO_OF_CARDS_IN_HAND)).drawHand();
    }

}

    @Test
    void testBeginGame() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);

        when(board.getPlayers()).thenReturn(List.of(player1, player2));

        gameController.beginGame();

        verify(player1, times(STARTING_UPGRADE_CARDS.size())).tryAddFreeUpgradeCard(any(UpgradeCard.class), eq(gameController));
    }
}*/

/*class GameControllerTest {

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Robots robot = Robots.getRobotByName("SPIN BOT");
        Space[][] spaces = new Space[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                spaces[i][j] = new Space(null, i, j, null);
            }
        }
        Board board = new Board(null, spaces, "TestBoard", 4);
        Runnable gameOverMethod = null;
        gameController = new GameController(board, gameOverMethod);
        Player player = new Player(board, robot, "testPlayer");
        board.addPlayer(player);
        player.setSpace(board.getSpace(0,0));
        player.setHeading(Heading.values()[0]);
        board.setCurrentPlayer(board.getPlayer(0));

        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, robot,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        //Player player2 = board.getPlayer(1);



        // gameController.movePlayerToSpace(, board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        //Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");

    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.queueCommand(Command.MOVE_1, gameController);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should be Space (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void fastForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.queueCommand(Command.MOVE_2, gameController);

        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(), "Player " + current.getName() + " should beSpace (0,2)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void turnRight() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.queueCommand(Command.RIGHT_TURN, gameController);

        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player " + current.getName() + " should beSpace (0,0)!");
        Assertions.assertEquals(Heading.WEST, current.getHeading(), "Player 0 should be heading WEST!");
    }

    @Test
    void turnLeft() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.queueCommand(Command.LEFT_TURN, gameController);

        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player " + current.getName() + " should beSpace (0,0)!");
        Assertions.assertEquals(Heading.EAST, current.getHeading(), "Player 0 should be heading EAST!");
    }

}*/