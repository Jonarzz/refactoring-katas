package io.github.jonarzz.kata.string.calculator.oop;

class NotEmptyValueAcceptor implements ValueAcceptancePolicy {

    @Override
    public boolean isInvalid(String value) {
        return value.isEmpty();
    }

    @Override
    public String formatErrorMessage(String value) {
        return "Separated values cannot be empty";
    }

}
