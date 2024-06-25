package cucumber_tests.changing_phases;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.Board;
import com.group15.roborally.client.model.CardField;
import com.group15.roborally.client.model.Phase;
import com.group15.roborally.client.model.Player;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

import static com.group15.roborally.client.BoardOptions.DRAW_ON_EMPTY_REGISTER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameControllerTestStepDefinitions {

    private Board board;
    private Runnable gameOverMethod;
    private GameController gameController;
    private Player player1;
    private Queue<Player> mockPriorityList;
    private AtomicReference<Phase> currentPhase;

    @Before
    public void setUp() {
        board = mock(Board.class);
        gameOverMethod = mock(Runnable.class);
        player1 = mock(Player.class);
        mockPriorityList = mock(Queue.class);
        gameController = new GameController(board, gameOverMethod);
        currentPhase = new AtomicReference<>();

        doAnswer(invocation -> {
            currentPhase.set(invocation.getArgument(0));
            return null;
        }).when(board).setCurrentPhase(any(Phase.class));
        when(board.getCurrentPhase()).thenAnswer(invocation -> currentPhase.get());

        when(mockPriorityList.peek()).thenReturn(player1);
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
        gameController.startProgrammingPhase();
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
            for (com.group15.roborally.client.model.Player player : board.getPlayers()) {
                player.fillRestOfRegisters();
            }
        }
    }
}
