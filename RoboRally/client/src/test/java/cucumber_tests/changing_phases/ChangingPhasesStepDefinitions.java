package cucumber_tests.changing_phases;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ChangingPhasesStepDefinitions {

    @Given("a game is running")
    public void a_game_is_running() {
    }

    @Given("the current phase is board activation")
    public void the_current_phase_is_board_activation() {
    }

    @When("action is taken to move to the next phase")
    public void action_is_taken_to_move_to_the_next_phase() {
    }

    @Then("the next phase should be upgrade")
    public void the_next_phase_should_be_upgrade() {
    }
}
