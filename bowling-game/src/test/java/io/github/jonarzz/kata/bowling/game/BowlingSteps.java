package io.github.jonarzz.kata.bowling.game;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class BowlingSteps {

    private Game game;

    @Given("game is started")
    public void gameIsStarted() {
        game = new SimpleGame();
    }

    @When("{int} pins knocked down in the first/second throw")
    public void knockPinsDown(int knockedDownPins) {
        game.roll(knockedDownPins);
    }

    @When("{int} pins knocked down {int} times")
    public void knockPinsDownMultipleTimes(int pinsPerThrow, int throwsCount) {
        for (int i = 0; i < throwsCount; i++) {
            knockPinsDown(pinsPerThrow);
        }
    }

    @Then("score is {int}")
    public void checkScore(int expectedScore) {
        assertThat(game.score())
                .isEqualTo(expectedScore);
    }

}
