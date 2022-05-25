package io.github.jonarzz.kata.string.calculator.oop;

interface LazyStatefulValidator {

    boolean validate(String value);

    void throwValidationError() throws IllegalArgumentException;

}
