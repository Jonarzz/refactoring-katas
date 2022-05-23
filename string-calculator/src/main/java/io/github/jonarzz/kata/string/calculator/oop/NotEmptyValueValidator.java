package io.github.jonarzz.kata.string.calculator.oop;

class NotEmptyValueValidator implements EagerValidator {

    @Override
    public void validate(String value) throws IllegalArgumentException {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Separated values cannot be empty");
        }
    }

}
