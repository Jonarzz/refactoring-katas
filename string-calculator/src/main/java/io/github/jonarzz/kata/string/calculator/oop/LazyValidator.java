package io.github.jonarzz.kata.string.calculator.oop;

interface LazyValidator {

    boolean validate(String value);

    void throwValidationError() throws IllegalArgumentException;

}
