package cucumber_tests.changing_phases;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.boardelements.BE_SpawnPoint;
import com.group15.roborally.client.view.BoardView;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

import static com.group15.roborally.client.BoardOptions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameControllerTestStepDefinitions {

    private Board board;
    private BoardView boardView;
    private Runnable gameOverMethod;
    private GameController gameController;
    private Player currentPlayer;
    private Queue<Player> mockPriorityList;
    private AtomicReference<Phase> currentPhase;
    private Space spawnSpace;

    @Before
    public void setUp() {
        board = mock(Board.class);
        boardView = mock(BoardView.class);
        spawnSpace = mock(Space.class);
        gameOverMethod = mock(Runnable.class);
        currentPlayer = mock(Player.class);
        mockPriorityList = mock(Queue.class);
        gameController = new GameController(board, gameOverMethod);
        currentPhase = new AtomicReference<>();

        doAnswer(invocation -> {
            currentPhase.set(invocation.getArgument(0));
            return null;
        }).when(board).setCurrentPhase(any(Phase.class));
        when(board.getCurrentPhase()).thenAnswer(invocation -> currentPhase.get());

        when(mockPriorityList.peek()).thenReturn(currentPlayer);
        when(board.getPriorityList()).thenReturn(mockPriorityList);
    }

    // Scenario: Start programming phase

    @Given("a game controller")
    public void aGameController() {
        // Initialization is done in the setUp method
    }

    @Given("the board is set up")
    public void theBoardIsSetUp() {
        // Board setup is mocked in the setUp method
    }

    @Given("there is a priority list with a player")
    public void thereIsAPriorityListWithAPlayer() {
        // Priority list is mocked in the setUp method
    }

    @When("the programming phase is started")
    public void theProgrammingPhaseIsStarted() {
        gameController.startProgrammingPhase();
    }

    @Then("the current phase should be PROGRAMMING")
    public void theCurrentPhaseShouldBePROGRAMMING() {
        assertEquals(Phase.PROGRAMMING, board.getCurrentPhase());
    }

    // Scenario: Finish programming phase and update board state

    @Given("the board is in the programming phase")
    public void the_board_is_in_the_programming_phase() {
        board.setCurrentPhase(Phase.PROGRAMMING);
    }

    @When("the finishProgrammingPhase method is called")
    public void the_finishProgrammingPhase_method_is_called() {
        gameController.finishProgrammingPhase();
    }

    @Then("the board should switch to the player activation phase")
    public void the_board_should_switch_to_the_player_activation_phase() {
        assertEquals(Phase.PLAYER_ACTIVATION, board.getCurrentPhase());
    }

    @Then("all program fields should be visible up to the first register")
    public void all_program_fields_should_be_visible_up_to_the_first_register() {
        for (Player player : board.getPlayers()) {
            for (int i = 0; i < Player.NO_OF_REGISTERS; i++) {
                CardField field = player.getProgramField(i);
                if (i == 0) {
                    assertTrue(field.isVisible());
                } else {
                    assertFalse(field.isVisible());
                }
            }
        }
    }

    @Then("players should fill the rest of their registers if draw on empty register is enabled")
    public void players_should_fill_the_rest_of_their_registers_if_draw_on_empty_register_is_enabled() {
        if (DRAW_ON_EMPTY_REGISTER) {
            for (Player player : board.getPlayers()) {
                player.fillRestOfRegisters();
            }
        }
    }

    // Scenario Outline: Choose direction in the initialization phase

    @Given("the board is in the initialization phase")
    public void the_board_is_in_the_initialization_phase() {

        when(board.getCurrentPhase()).thenReturn(Phase.INITIALIZATION);
        NO_OF_PLAYERS = 2;

        when(board.getCurrentPlayer()).thenReturn(currentPlayer);
        when(currentPlayer.getSpace()).thenReturn(spawnSpace);

        for (int i = 1; i <= NO_OF_PLAYERS; i++) {
            Player player = mock(Player.class);
            Space playerSpace = mock(Space.class);
            when(board.getPlayer(i)).thenReturn(player);
            when(player.getSpace()).thenReturn(playerSpace);
            when(playerSpace.getBoardElement()).thenReturn(mock(BE_SpawnPoint.class));
        }
    }

    @When("the player chooses the {word} direction")
    public void the_player_chooses_the_direction(String direction) {
        Heading heading = Heading.valueOf(direction);
        gameController.chooseDirection(heading, boardView);
    }

    @Then("the board should set the current player's heading to {word}")
    public void the_board_should_set_the_current_player_s_heading_to(String direction) {
        Heading expectedHeading = Heading.valueOf(direction);
        assertEquals(expectedHeading, board.getCurrentPlayer().getHeading());
    }
}





/*    @Given("the board is in the initialization phase with current player {int}")
    public void the_board_is_in_the_initialization_phase_with_current_player(int currentPlayerIndex) {

        NO_OF_PLAYERS = 4;

        when(board.getCurrentPhase()).thenReturn(Phase.INITIALIZATION);

        currentPlayer = mock(Player.class);
        Space spawnSpace = mock(Space.class);
        when(currentPlayer.getSpace()).thenReturn(spawnSpace);
        when(board.getCurrentPlayer()).thenReturn(currentPlayer);
        when(board.getPlayerNumber(currentPlayer)).thenReturn(currentPlayerIndex);

        for (int i = 1; i <= NO_OF_PLAYERS; i++) {
            Player player = mock(Player.class);
            Space playerSpace = mock(Space.class);
            when(board.getPlayer(i)).thenReturn(player);
            when(player.getSpace()).thenReturn(playerSpace);
            when(playerSpace.getBoardElement()).thenReturn(mock(BE_SpawnPoint.class));
        }
    }

    @When("the player chooses the {word} direction")
    public void the_player_chooses_the_direction(String direction) {
        Heading heading = Heading.valueOf(direction);
        gameController.chooseDirection(heading, boardView);
    }

    @Then("the board should set the current player's heading to {word}")
    public void the_board_should_set_the_current_player_s_heading_to(String heading) {
        Heading expectedHeading = Heading.valueOf(heading);
        assertEquals(expectedHeading, board.getCurrentPlayer().getHeading());
    }

    @Then("the current player should be updated to {int}")
    public void the_current_player_should_be_updated_to(int nextPlayerIndex) {
        Player nextPlayer = board.getPlayer(nextPlayerIndex);
        assertEquals(nextPlayer, board.getCurrentPlayer());
    }
}
*/