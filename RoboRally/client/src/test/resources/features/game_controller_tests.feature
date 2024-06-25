Feature: Game Controller Programming Phase

  Scenario: Start programming phase
    Given a game controller
    And the board is set up
    And there is a priority list with a player
    When the programming phase is started
    Then the current phase should be PROGRAMMING

  Scenario: Finish programming phase and update board state
    Given the board is in the programming phase
    When the finishProgrammingPhase method is called
    Then the board should switch to the player activation phase
    And all program fields should be visible up to the first register
    And players should fill the rest of their registers if draw on empty register is enabled

  Scenario Outline: Choose direction in the initialization phase
    Given the board is in the initialization phase
    When the player chooses the <direction> direction
    Then the board should set the current player's heading to <direction>
    Examples:
      | direction |
      | NORTH     |
      | EAST      |
      | SOUTH     |
      | WEST      |


#  Scenario Outline: Choose direction in the initialization phase
#    Given the board is in the initialization phase with current player <currentPlayer>
#    When the player chooses the <direction> direction
#    Then the board should set the current player's heading to <direction>
#    And the current player should be updated to <nextPlayer>
#
#    Examples:
#      | currentPlayer | direction | nextPlayer |
#      | 1             | NORTH     | 2          |
#      | 2             | EAST      | 3          |
#      | 3             | SOUTH     | 4          |
#      | 4             | WEST      | 1          |
