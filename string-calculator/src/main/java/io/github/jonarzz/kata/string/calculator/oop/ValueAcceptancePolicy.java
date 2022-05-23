package io.github.jonarzz.kata.string.calculator.oop;

interface ValueAcceptancePolicy {

    boolean isInvalid(String value);

    String formatErrorMessage(String value);

}
