package io.github.jonarzz.kata.string.calculator;

import static java.util.function.Predicate.not;

import java.util.Arrays;

class SplitStreamStringCalculator implements StringCalculator {

    @Override
    public int add(String numbers) {
        return Arrays.stream(numbers.split(","))
                     .filter(not(String::isEmpty))
                     .mapToInt(Integer::parseInt)
                     .sum();
    }

}
