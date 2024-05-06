package dk.dtu.compute.se.pisd.roborally.Gherkin;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.*;
import io.cucumber.java.en.*;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

public class Stepdefs extends ApplicationTest {
    private Board board;
    private GameController gc;
    private List<Player> players;

    // RoboRally rb = new RoboRally();

    //    @Override
    //    public void start(Stage stage) {
    //        rb.start(stage);
    //        rb.createBoardView(gc);
    //        stage.show();
    //    }

    @Given("The application has started")
    public void applicationHasStarted() throws Exception {
        // ApplicationTest.launch(RoboRally.class);
        board = new Board(8, 8);

        players = new ArrayList<>();
        players.add(new Player(board, "red", "player 1"));
        players.add(new Player(board, "yellow", "player 2"));
        players.add(new Player(board, "blue", "player 3"));

        board.addPlayer(players.get(0));
        board.addPlayer(players.get(1));
        board.addPlayer(players.get(2));

        board.getSpace(0, 0).setPlayer(players.get(0));
        board.getSpace(0, 1).setPlayer(players.get(1));
        board.getSpace(0, 2).setPlayer(players.get(2));

        gc = new GameController(board);

        board.setCurrentPlayer(board.getPlayer(0));
    }

    @When("The space \\(x: {int}, y: {int}) is clicked")
    public void SpaceIsClicked(int x, int y) {
        gc.movePlayerToSpace(new Player(board, null, null), board.getSpace(x,y));
    }

    @Then("Player {int} is in position \\(x: {int}, y: {int})")
    public void playerIsInPositionXY(int playerNum, int x, int y) {
        assert board.getSpace(x,y).getPlayer() == players.get(playerNum + 1);
    }
}
