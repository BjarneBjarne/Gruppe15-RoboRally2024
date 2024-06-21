Feature: Phases change properly

  The phases of the game are changing between initialization, programming,
  player activation, board activation and upgrade.

  Background:
    Given a game is running

  Scenario: Start upgrading phase
    Given the current phase is board activation
    When action is taken to move to the next phase
    Then the next phase should be upgrade