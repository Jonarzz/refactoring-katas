Feature: Score can range from 0 to 300

  Background:
    Given new game is started

  Scenario: No pin is knocked down in any frame
    When 0 pins knocked down in standard 10 frames
    Then score is 0

  Scenario: Highest possible score without neither strike nor spare
    When 9 pins knocked down in standard 10 frames
    Then score is 90

  Scenario: All frames are spares and the 10th frame extra roll is 0
    When 5 pins knocked down in standard 10 frames
    And 0 pins knocked down in the next roll
    Then score is 145

  Scenario: All frames are spares and the 10th frame extra roll is 10
    When 5 pins knocked down in standard 10 frames
    And 10 pins knocked down in the next roll
    Then score is 155

  Scenario: All rolls are strikes (except the 10th frame extra roll)
    When 10 pins knocked down in standard 10 frames
    And 0 pins knocked down in the next roll
    Then score is 290

  Scenario: All rolls are strikes (including the 10th frame extra roll)
    When 10 pins knocked down in standard 10 frames
    And 10 pins knocked down in the next roll
    Then score is 300
