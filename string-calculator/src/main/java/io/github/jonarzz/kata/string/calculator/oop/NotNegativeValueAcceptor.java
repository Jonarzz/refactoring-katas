package io.github.jonarzz.kata.string.calculator.oop;

class NotNegativeValueAcceptor implements ValueAcceptancePolicy {

    @Override
    public boolean isInvalid(String value) {
        return value.startsWith("-");
    }

    @Override
    public String formatErrorMessage(String value) {
        return "Negatives not allowed, but got: " + value;
    }

}
