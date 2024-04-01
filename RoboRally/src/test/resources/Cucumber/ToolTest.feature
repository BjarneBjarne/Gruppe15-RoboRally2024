Feature: Can players move by clicking the board?
Background:
Given The application has started
  Scenario: Move player by clicking board
  When The space (x: 3, y: 3) is clicked
  Then Player 1 is in position (x: 3, y: 3)
  When The space (x: 4, y: 4) is clicked
  Then Player 2 is in position (x: 4, y: 4)
  When The space (x: 5, y: 5) is clicked
  Then Player 3 is in position (x: 5, y: 5)

