package dk.dtu.compute.se.pisd.roborally.Gherkin;

import gruppe15.roborally.RoboRally;
import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class Stepdefs extends ApplicationTest {
    private Board brd;
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
        brd = new Board(8, 8);

        players = new ArrayList<>();
        players.add(new Player(brd, "red", "player 1"));
        players.add(new Player(brd, "yellow", "player 2"));
        players.add(new Player(brd, "blue", "player 3"));

        brd.addPlayer(players.get(0));
        brd.addPlayer(players.get(1));
        brd.addPlayer(players.get(2));

        brd.getSpace(0, 0).setPlayer(players.get(0));
        brd.getSpace(0, 1).setPlayer(players.get(1));
        brd.getSpace(0, 2).setPlayer(players.get(2));

        gc = new GameController(brd);

        brd.setCurrentPlayer(brd.getPlayer(0));
    }

    @When("The space \\(x: {int}, y: {int}) is clicked")
    public void SpaceIsClicked(int x, int y) {
        gc.moveCurrentPlayerToSpace(brd.getSpace(x,y));
    }

    @Then("Player {int} is in position \\(x: {int}, y: {int})")
    public void playerIsInPositionXY(int playerNum, int x, int y) {
        assert brd.getSpace(x,y).getPlayer() == players.get(playerNum + 1);
    }
}
