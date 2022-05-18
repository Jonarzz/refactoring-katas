package io.github.jonarzz.kata.string.calculator;

import java.util.Arrays;

class SplitStreamStringCalculator implements StringCalculator {

    @Override
    public int add(String numbers) {
        if (numbers.isEmpty()) {
            return 0;
        }
        return Arrays.stream(numbers.split("[\\n,]", -1))
                     .peek(this::validate)
                     .mapToInt(Integer::parseInt)
                     .sum();
    }

    private void validate(String value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Separated values cannot be empty");
        }
    }

}
