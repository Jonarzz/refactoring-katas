package io.github.jonarzz.kata.bowling.game;

// TODO needs to be fully implemented after creating all test scenarios
public class SimpleGame implements Game {

    private int totalKnockedDownPins;

    @Override
    public void roll(int knockedDownPins) {
        totalKnockedDownPins += knockedDownPins;
    }

    @Override
    public int score() {
        return totalKnockedDownPins;
    }

}
