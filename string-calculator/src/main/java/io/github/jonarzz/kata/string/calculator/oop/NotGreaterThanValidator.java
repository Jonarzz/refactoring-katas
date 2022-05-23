package io.github.jonarzz.kata.string.calculator.oop;

import static java.lang.Integer.parseInt;

class NotGreaterThanValidator implements LazyValidator {

    private int maxValue;

    NotGreaterThanValidator(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public boolean validate(String value) {
        return parseInt(value) <= maxValue;
    }

    @Override
    public void throwValidationError() throws IllegalArgumentException {
        // value is not accepted, but no exception is thrown
    }

}
