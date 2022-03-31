package io.github.jonarzz.kata.bowling.game.frame;

class Roll {

    static final int MAX_PINS = 10;

    private int number;
    private int knockedDownPins;

    private Roll(int number, int knockedDownPins) {
        this.number = number;
        this.knockedDownPins = knockedDownPins;
    }

    static RollCreator number(int number) {
        return new RollCreator(number);
    }

    int number() {
        return number;
    }

    int knockedDownPins() {
        return knockedDownPins;
    }

    boolean isStrike() {
        return MAX_PINS == knockedDownPins;
    }

    static class RollCreator {

        private int rollNumber;

        private RollCreator(int rollNumber) {
            this.rollNumber = rollNumber;
        }

        Roll knockDown(int pins) {
            if (pins > MAX_PINS) {
                throw new IllegalArgumentException("Max pins that can be knocked down in one roll is " + MAX_PINS);
            }
            return new Roll(rollNumber, pins);
        }

    }

}
