package io.github.jonarzz.kata.string.calculator.oop;

interface ValueOmittingValidator extends LazyStatefulValidator {

    @Override
    default void throwValidationError() throws IllegalArgumentException {
        // value is not accepted, but no exception is thrown
    }

}
