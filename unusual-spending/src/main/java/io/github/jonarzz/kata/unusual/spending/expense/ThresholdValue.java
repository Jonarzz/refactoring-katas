package io.github.jonarzz.kata.unusual.spending.expense;

public class ThresholdValue {

    private double value;

    private ThresholdValue(double value) {
        this.value = value;
    }

    public static ThresholdValue proportion(double value) {
        return new ThresholdValue(value);
    }

    public static ThresholdValue percentage(double value) {
        return proportion(value / 100d);
    }

    double value() {
        return value;
    }
}
