package io.github.jonarzz.kata.string.calculator.oop;

import static java.lang.String.join;

import java.util.ArrayList;
import java.util.List;

class NotNegativeValueValidator implements LazyStatefulValidator {

    private List<String> invalidValues = new ArrayList<>();

    @Override
    public boolean validate(String value) {
        if (value.startsWith("-")) {
            invalidValues.add(value);
            return false;
        }
        return true;
    }

    @Override
    public void throwValidationError() {
        if (!invalidValues.isEmpty()) {
            throw new IllegalArgumentException("Negatives not allowed, but got: " + join(", ", invalidValues));
        }
    }

}
