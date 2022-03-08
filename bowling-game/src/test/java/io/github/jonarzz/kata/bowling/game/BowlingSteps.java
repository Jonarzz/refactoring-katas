package io.github.jonarzz.kata.bowling.game;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class BowlingSteps {

    private Game game;

    @Given("new game is started")
    public void gameIsStarted() {
        game = new SimpleGame();
    }

    @When("{int} pin(s) knocked down in the first/second/next roll")
    public void knockPinsDown(int knockedDownPins) {
        game.roll(knockedDownPins);
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
