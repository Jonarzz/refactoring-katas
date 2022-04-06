package io.github.jonarzz.kata.bowling.game.frame;

import static io.github.jonarzz.kata.bowling.game.frame.FrameFactory.MAX_PINS_PER_FRAME;

public class Roll {

    private int number;
    private int knockedDownPins;

    private Roll(int number, int knockedDownPins) {
        this.number = number;
        this.knockedDownPins = knockedDownPins;
    }

    public static RollCreator number(int number) {
        return new RollCreator(number);
    }

    int number() {
        return number;
    }

    int knockedDownPins() {
        return knockedDownPins;
    }

    boolean isStrike() {
        return MAX_PINS_PER_FRAME == knockedDownPins;
    }

    public static class RollCreator {

        private int rollNumber;

        private RollCreator(int rollNumber) {
            this.rollNumber = rollNumber;
        }

        public Roll knockDown(int pins) {
            if (pins > MAX_PINS_PER_FRAME) {
                throw new IllegalArgumentException("Max pins that can be knocked down in one roll is " + MAX_PINS_PER_FRAME);
            }
            return new Roll(rollNumber, pins);
        }

    }

}
