Feature: Simple game without spares and strikes

  The game consists of 10 frames. In each frame the player has two rolls to knock down 10 pins (20 rolls total).
  The score for the game without spares and strikes is the total number of pins knocked down.

  Scenario Outline: Score is calculated after knocking down the same amount of pins in each roll
    Given new game is started
    When <pins_per_roll> pins knocked down 20 times
    Then score is <score>

    Examples:
      | pins_per_roll | score |
      | 0             | 0     |
      | 1             | 20    |
      | 2             | 40    |
      | 3             | 60    |
      | 4             | 80    |
