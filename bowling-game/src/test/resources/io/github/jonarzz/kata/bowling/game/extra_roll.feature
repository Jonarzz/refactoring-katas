Feature: Extra roll

  Scenario Outline: In the tenth frame a player who rolls a spare or strike is allowed to roll extra balls to complete the frame. No extra points are added.
    Given new game is started
    And 0 pins knocked down 18 times
    When <first_roll_pins> pins knocked down in the first roll
    And <second_roll_pins> pins knocked down in the second roll
    And <third_roll_pins> pins knocked down in the next roll
    Then score is <score>

    Examples:
      | first_roll_pins | second_roll_pins | third_roll_pins | score |
      | 1               | 9                | 0               | 10    |
      | 3               | 7                | 3               | 13    |
      | 4               | 6                | 6               | 16    |
      | 5               | 5                | 8               | 18    |
      | 8               | 2                | 10              | 20    |

  Rule: No more than three balls can be rolled in the tenth frame.

    Scenario: Try to roll more than 3 times in a perfect tenth frame
      Given new game is started
      And 0 pins knocked down 18 times
      And 10 pins knocked down 3 times
      When 10 pins knocked down 5 times
      Then score is 30

  Rule: When neither spare nor strike is rolled, no extra ball is allowed

    Scenario: Try to roll more than 2 times in a tenth frame without spare/strike
      Given new game is started
      And 0 pins knocked down 18 times
      And 3 pins knocked down 2 times
      When 10 pins knocked down in the next roll
      Then score is 6
