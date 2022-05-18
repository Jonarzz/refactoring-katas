package io.github.jonarzz.kata.string.calculator.oop;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;

class SeparatedValues<T> {

    private Collection<T> values;

    private SeparatedValues(Collection<T> values) {
        this.values = values;
    }

    static SeparatedValues<Integer> notEmptyInts(String separatedValues, Delimiter delimiter) {
        if (separatedValues.isEmpty()) {
            return new SeparatedValues<>(Set.of());
        }
        var ints = Arrays.stream(delimiter.split(separatedValues))
                         .peek(SeparatedValues::validateNotEmpty)
                         .map(Integer::parseInt)
                         .toList();
        return new SeparatedValues<>(ints);
    }

    @SuppressWarnings("SameParameterValue")
    <R> R reduce(R initialValue, BiFunction<R, T, R> accumulator) {
        return values.stream()
                     .reduce(initialValue, accumulator, (a, b) -> a);
    }

    private static void validateNotEmpty(String value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Separated values cannot be empty");
        }
    }

}
