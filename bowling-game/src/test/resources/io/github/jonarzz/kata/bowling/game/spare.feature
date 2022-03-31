Feature: Spare

  A spare is when the player knocks down all 10 pins in two rolls.

  Scenario Outline: Bonus for the frame with a spare is the number of pins knocked down by the first roll in the next frame.
    Given new game is started
    When <first_roll_pins> pins knocked down in the first roll
    And <second_roll_pins> pins knocked down in the second roll
    And <third_roll_pins> pins knocked down in the next roll
    And 0 pins knocked down 17 times
    Then score is <score>

    Examples:
      | first_roll_pins | second_roll_pins | third_roll_pins | score |
      | 1               | 9                | 0               | 10    |
      | 3               | 7                | 3               | 16    |
      | 4               | 6                | 6               | 22    |
      | 5               | 5                | 8               | 26    |
      | 8               | 2                | 10              | 30    |

  Rule: No bonus points for spare within the last frame.

    Scenario Outline: No bonus points for spare within the last frame.
      Given new game is started
      And 0 pins knocked down 18 times
      When <first_roll_pins> pins knocked down in the first roll
      And <second_roll_pins> pins knocked down in the second roll
      And <third_roll_pins> pins knocked down in the next roll
      Then score is <score>

      Examples:
        | first_roll_pins | second_roll_pins | third_roll_pins | score |
        | 4               | 6                | 1               | 11    |
        | 5               | 5                | 7               | 17    |
        | 7               | 3                | 10              | 20    |