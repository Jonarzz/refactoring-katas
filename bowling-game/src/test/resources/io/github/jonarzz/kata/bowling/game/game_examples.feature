Feature: Various game examples

  Scenario: No spare/strike game
    Given new game is started
    # frame 1
    When 3 pins knocked down in the first roll
    And 2 pins knocked down in the second roll
    # frame 2
    * 9 pins knocked down in the next roll
    * 0 pins knocked down in the next roll
    # frame 3
    * 0 pins knocked down in the next roll
    * 0 pins knocked down in the next roll
    # frame 4
    * 1 pins knocked down in the next roll
    * 1 pins knocked down in the next roll
    # frame 5
    * 2 pins knocked down in the next roll
    * 3 pins knocked down in the next roll
    # frame 6
    * 4 pins knocked down in the next roll
    * 5 pins knocked down in the next roll
    # frame 7
    * 5 pins knocked down in the next roll
    * 1 pins knocked down in the next roll
    # frame 8
    * 2 pins knocked down in the next roll
    * 1 pins knocked down in the next roll
    # frame 9
    * 7 pins knocked down in the next roll
    * 0 pins knocked down in the next roll
    # frame 10
    * 4 pins knocked down in the next roll
    * 4 pins knocked down in the next roll
    Then score is 54

  Scenario: Highest possible score without neither strike nor spare
    Given new game is started
    # frame 1
    When 1 pins knocked down in the first roll
    * 8 pins knocked down in the second roll
    # frame 2
    * 2 pins knocked down in the next roll
    * 7 pins knocked down in the next roll
    # frame 3
    * 3 pins knocked down in the next roll
    * 6 pins knocked down in the next roll
    # frame 4
    * 4 pins knocked down in the next roll
    * 5 pins knocked down in the next roll
    # frame 5
    * 5 pins knocked down in the next roll
    * 4 pins knocked down in the next roll
    # frame 6
    * 6 pins knocked down in the next roll
    * 3 pins knocked down in the next roll
    # frame 7
    * 7 pins knocked down in the next roll
    * 2 pins knocked down in the next roll
    # frame 8
    * 8 pins knocked down in the next roll
    * 1 pins knocked down in the next roll
    # frame 9
    * 1 pins knocked down in the next roll
    * 8 pins knocked down in the next roll
    # frame 10
    * 2 pins knocked down in the next roll
    * 7 pins knocked down in the next roll
    Then score is 90
