Feature: Strike

  A strike is when the player knocks down all 10 pins on their first roll within frame.
  The frame is then completed with a single roll.

  Rule: Bonus for the frame with a strike is the value of the next two rolls.

    Scenario Outline: Second roll is not a strike
      Given new game is started
      When 10 pins knocked down in the first roll
      And <second_roll_pins> pins knocked down in the second roll
      And <third_roll_pins> pins knocked down in the next roll
      And 0 pins knocked down 17 times
      Then score is <score>

      Examples:
        | second_roll_pins | third_roll_pins | score |
        | 0                | 0               | 10    |
        | 1                | 0               | 12    |
        | 0                | 1               | 12    |
        | 1                | 1               | 14    |
        | 3                | 2               | 20    |
        | 5                | 5               | 30    |

    Scenario Outline: Second roll is a strike
      Given new game is started
      When 10 pins knocked down in the first roll
      And 10 pins knocked down in the second roll
      And <third_roll_pins> pins knocked down in the next roll
      And <fourth_roll_pins> pins knocked down in the next roll
      And 0 pins knocked down 16 times
      Then score is <score>

      Examples:
        | third_roll_pins | fourth_roll_pins | score |
        | 0               | 0                | 30    |
        | 1               | 0                | 33    |
        | 1               | 1                | 35    |
        | 2               | 2                | 40    |
        | 5               | 5                | 55    |
        | 0               | 9                | 48    |
        | 0               | 10               | 50    |
        | 9               | 0                | 57    |
        | 9               | 1                | 59    |
        | 10              | 0                | 60    |
        | 10              | 10               | 90    |

  Rule: No bonus points for strike within the last frame.

    Scenario Outline: No bonus points for strike within the last frame.
      Given new game is started
      And 0 pins knocked down 18 times
      When <first_roll_pins> pins knocked down in the first roll
      And <second_roll_pins> pins knocked down in the second roll
      And <third_roll_pins> pins knocked down in the next roll
      Then score is <score>

      Examples:
        | first_roll_pins | second_roll_pins | third_roll_pins | score |
        | 10              | 0                | 0               | 10    |
        | 0               | 10               | 0               | 10    |
        | 10              | 10               | 0               | 20    |
        | 10              | 0                | 10              | 20    |
        | 0               | 10               | 10              | 20    |
        | 10              | 10               | 10              | 30    |