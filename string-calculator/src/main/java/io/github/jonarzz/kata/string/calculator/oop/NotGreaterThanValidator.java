package io.github.jonarzz.kata.string.calculator.oop;

import static java.lang.Integer.parseInt;

class NotGreaterThanValidator implements ValueOmittingValidator {

    private final int maxValue;

    NotGreaterThanValidator(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public boolean validate(String value) {
        return parseInt(value) <= maxValue;
    }

}
