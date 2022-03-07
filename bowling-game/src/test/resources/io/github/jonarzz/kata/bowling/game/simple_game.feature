Feature: Simple game without spares and strikes

  The game consists of 10 frames. In each frame the player has two rolls to knock down 10 pins.
  The score for the frame is the total number of pins knocked down.

  Background:
    Given game is started

  Scenario Outline: Score calculation in the first frame
    When <first_throw_pins> pins knocked down in the first throw
    And <second_throw_pins> pins knocked down in the second throw
    Then score is <score>

  Examples:
    | first_throw_pins | second_throw_pins | score |
    | 0                | 0                 | 0     |
    | 1                | 0                 | 1     |
    | 0                | 1                 | 1     |
    | 1                | 1                 | 2     |
    | 5                | 3                 | 8     |
    | 4                | 5                 | 9     |

  Scenario Outline: Score calculation when knocking down same pins count in multiple frames
    When <pins_per_throw> pins knocked down <throws_count> times
    Then score is <score>

  Examples:
    | pins_per_throw | throws_count | score |
    | 5              | 3            | 15    |
    | 7              | 9            | 63    |
    | 0              | 20           | 0     |
    | 1              | 20           | 20    |
    | 9              | 20           | 180   |
