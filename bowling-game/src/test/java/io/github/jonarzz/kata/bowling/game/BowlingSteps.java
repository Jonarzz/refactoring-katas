package io.github.jonarzz.kata.bowling.game;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.jonarzz.kata.bowling.game.frame.DefaultGame;

import java.security.SecureRandom;

public class BowlingSteps {

    private static final int FRAMES_PER_GAME = 10;

    private static final SecureRandom RANDOM = new SecureRandom();

    private Game game;

    @Given("new game is started")
    public void gameIsStarted() {
        game = new DefaultGame();
    }

    @When("{int} pin(s) knocked down in the first/second/next roll")
    public void knockPinsDown(int knockedDownPins) {
        game.roll(knockedDownPins);
    }

    @When("{int} pins knocked down in standard 10 frames")
    public void playFullGameWithSamePointsPerFrame(int pinsPerFrame) {
        var upperBound = pinsPerFrame + 1;
        var firstRollPinsCount = RANDOM.nextInt(0, upperBound);
        var secondRollPinsCount = pinsPerFrame - firstRollPinsCount;
        for (int i = 0; i < FRAMES_PER_GAME; i++) {
            game.roll(firstRollPinsCount);
            game.roll(secondRollPinsCount);
        }
    }

    @When("{int} pin(s) knocked down {int} times")
    public void knockPinsDownMultipleTimes(int pinsPerRoll, int rollsCount) {
        for (int i = 0; i < rollsCount; i++) {
            knockPinsDown(pinsPerRoll);
        }
    }

    @Then("score is {int}")
    public void checkScore(int expectedScore) {
        assertThat(game.score())
                .isEqualTo(expectedScore);
    }

}
