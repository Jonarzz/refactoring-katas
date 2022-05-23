package io.github.jonarzz.kata.string.calculator.oop;

class ThrowingValueValidator {

    private ValueAcceptancePolicy valueAcceptancePolicy;

    ThrowingValueValidator(ValueAcceptancePolicy valueAcceptancePolicy) {
        this.valueAcceptancePolicy = valueAcceptancePolicy;
    }

    final void validate(String value) throws IllegalArgumentException {
        if (valueAcceptancePolicy.isInvalid(value)) {
            throw new IllegalArgumentException(valueAcceptancePolicy.formatErrorMessage(value));
        }
    }

}
