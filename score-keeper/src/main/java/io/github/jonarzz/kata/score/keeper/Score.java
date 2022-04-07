package io.github.jonarzz.kata.score.keeper;

import java.util.concurrent.atomic.AtomicInteger;

class Score {

    private final String formatString;
    private final int maxValue;

    private final AtomicInteger value = new AtomicInteger();

    private Score(int maxDigits) {
        formatString = "%0" + maxDigits + "d";
        maxValue = (int) (Math.pow(10, maxDigits) - 1);
    }

    static Score withMaxDigits(int maxDigits) {
        return new Score(maxDigits);
    }

    void add(int points) {
        if (points <= 0) {
            return;
        }
        value.addAndGet(points);
    }

    @Override
    public String toString() {
        return formatString.formatted(
                Math.min(value.get(), maxValue)
        );
    }

}
