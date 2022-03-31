Feature: Score can range from 0 to 300

  Scenario: No pin is knocked down in any frame
    Given new game is started
    When 0 pins knocked down in standard 10 frames
    Then score is 0

  Scenario: Highest possible score without neither strike nor spare
    Given new game is started
    When 9 pins knocked down in standard 10 frames
    Then score is 90

  Scenario: All frames are spares and the 10th frame extra roll is 0
    Given new game is started
    When 5 pins knocked down 20 times
    And 0 pins knocked down in the next roll
    Then score is 145

  Scenario: All frames are spares and the 10th frame extra roll is 10
    Given new game is started
    When 5 pins knocked down 20 times
    And 10 pins knocked down in the next roll
    Then score is 155

  Scenario: All rolls are strikes (except the 10th frame extra rolls)
    Given new game is started
    When 10 pins knocked down 10 times
    And 0 pins knocked down 2 times
    Then score is 270

  Scenario: All rolls are strikes (except first of 10th frame extra rolls)
    Given new game is started
    When 10 pins knocked down 10 times
    And 0 pins knocked down in the next roll
    And 10 pins knocked down in the next roll
    Then score is 280

  Scenario: All rolls are strikes (except second of 10th frame extra rolls)
    Given new game is started
    When 10 pins knocked down 10 times
    And 10 pins knocked down in the next roll
    And 0 pins knocked down in the next roll
    Then score is 290

  Scenario: All rolls are strikes (including two 10th frame extra rolls)
    Given new game is started
    When 10 pins knocked down 10 times
    And 10 pins knocked down 2 times
    Then score is 300
