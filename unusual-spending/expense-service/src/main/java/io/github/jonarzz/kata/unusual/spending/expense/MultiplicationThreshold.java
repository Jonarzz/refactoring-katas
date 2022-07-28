package io.github.jonarzz.kata.unusual.spending.expense;

public class MultiplicationThreshold {

    private double value;

    private MultiplicationThreshold(double value) {
        this.value = value;
    }

    public static MultiplicationThreshold proportion(double value) {
        return new MultiplicationThreshold(value);
    }

    public static MultiplicationThreshold percentage(double value) {
        return proportion(value / 100d);
    }

    double value() {
        return value;
    }
}
